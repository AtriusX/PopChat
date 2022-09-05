package xyz.atrius.service.auth

import arrow.core.left
import arrow.core.right
import com.github.guepardoapps.kulid.ULID
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisConnectionException
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.message.ServerMessage
import xyz.atrius.service.gen.TokenGenService
import xyz.atrius.util.Message
import xyz.atrius.util.mockkRedisClient
import xyz.atrius.util.shouldBeMessage
import xyz.atrius.util.shouldBeOk
import xyz.atrius.validation.TokenRule

class TokenServiceSpec : DescribeSpec({

    describe("TokenService tests") {

        lateinit var testUlid: ULIDIdentifier
        lateinit var testToken: String
        lateinit var redisClient: RedisClient
        lateinit var tokenGenService: TokenGenService
        lateinit var tokenValidationRule: TokenRule
        lateinit var service: TokenService

        beforeTest {
            testUlid = ULID.random()
            testToken = "new_token"
            redisClient = mockkRedisClient {
                every { connect().sync().set(any(), any()) } returns "OK"
                every { connect().sync().del(any()) } returns 1
            }
            tokenGenService = mockk {
                every { generateToken() } returns testToken
            }
            tokenValidationRule = mockk {
                every { validate(any()) } returns ServerMessage.Ok.right()
            }
            service = spyk(
                TokenService(
                    redisClient,
                    tokenGenService,
                    tokenValidationRule
                )
            )
        }

        describe("authorize") {
            lateinit var result: Message<String>

            describe("A ULID is successfully authorized") {
                beforeTest {
                    result = service.authorize(testUlid)
                }

                it("Should return correctly") {
                    result shouldBeRight testToken
                }

                it("Should have set token") {
                    verify(exactly = 1) {
                        redisClient.connect().sync().set(any(), any())
                    }
                }
            }

            describe("When the redis client fails to set a token") {
                beforeTest {
                    every { redisClient.connect().sync().set(any(), any()) } returns "Fail"
                    result = service.authorize(testUlid)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.ExternalError>()
                }
            }

            describe("When the redis client encounters an error") {
                beforeTest {
                    every { redisClient.connect() } throws RedisConnectionException("Fail")
                    result = service.authorize(testUlid)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.ExternalError>()
                }
            }
        }

        describe("isAuthorized") {
            lateinit var result: Message<ULIDIdentifier>

            describe("A request is authorized for the given values") {
                beforeTest {
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return correctly") {
                    result shouldBeRight testUlid
                }
            }

            describe("When the provided ULID could not be found") {
                beforeTest {
                    every {
                        tokenValidationRule.validate(any())
                    } returns ServerMessage.NotFound(ULIDIdentifier::class, testUlid).left()
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.NotFound>()
                }
            }

            describe("When the provided token does not match the cached value") {
                beforeTest {
                    every {
                        tokenValidationRule.validate(any())
                    } returns ServerMessage.NotAuthorized(testToken).left()
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.NotAuthorized>()
                }
            }

            describe("When the redis client encounters an error") {
                beforeTest {
                    every {
                        tokenValidationRule.validate(any())
                    } returns ServerMessage.ExternalError("Fail").left()
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.ExternalError>()
                }
            }
        }

        describe("drop") {
            lateinit var result: Message<ServerMessage>

            describe("A token is successfully dropped") {
                beforeTest {
                    every { service.isAuthorized(any(), any()) } returns testUlid.right()
                    result = service.drop(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeOk()
                }

                it("Should delete the token") {
                    verify(exactly = 1) {
                        redisClient.connect().sync().del(any())
                    }
                }
            }

            describe("When the user is not authorized for the provided request") {
                beforeTest {
                    every {
                        service.isAuthorized(any(), any())
                    } returns ServerMessage.NotAuthorized(testToken).left()
                    result = service.drop(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.NotAuthorized>()
                }
            }

            describe("When the redis client encounters an error") {
                beforeTest {
                    every { service.isAuthorized(any(), any()) } returns testUlid.right()
                    every { redisClient.connect() } throws RedisConnectionException("fail")
                    result = service.drop(testUlid, testToken)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.ExternalError>()
                }
            }
        }
    }
})
