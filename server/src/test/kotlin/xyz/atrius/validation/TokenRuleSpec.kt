package xyz.atrius.validation

import com.github.guepardoapps.kulid.ULID
import io.kotest.core.spec.style.DescribeSpec
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisConnectionException
import io.mockk.every
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.mockkRedisClient
import xyz.atrius.util.shouldBeMessage
import xyz.atrius.util.shouldBeOk
import xyz.atrius.validation.TokenRule.TokenRuleRequest

class TokenRuleSpec : DescribeSpec({

    describe("TokenRule tests") {

        lateinit var testUlid: ULIDIdentifier
        lateinit var testToken: String
        lateinit var redisClient: RedisClient
        lateinit var validator: TokenRule

        beforeTest {
            testUlid = ULID.random()
            testToken = "foo_token"
            redisClient = mockkRedisClient {
                every { connect().sync().get(any()) } returns testToken
            }
            validator = TokenRule(
                redisClient
            )
        }

        describe("validate") {
            lateinit var result: Message<ServerMessage.Ok>

            describe("A token is successfully authorized") {
                beforeTest {
                    result = validator.validate(
                        TokenRuleRequest(
                            testUlid,
                            testToken
                        )
                    )
                }

                it("Should return Ok") {
                    result.shouldBeOk()
                }
            }

            describe("When the client cannot find the requested ULID") {
                beforeTest {
                    every { redisClient.connect().sync().get(any()) } returns null
                    result = validator.validate(
                        TokenRuleRequest(
                            testUlid,
                            testToken
                        )
                    )
                }

                it("Should return Ok") {
                    result.shouldBeMessage<ServerMessage.NotFound>()
                }
            }

            describe("When the token does not match the cached value") {
                beforeTest {
                    every { redisClient.connect().sync().get(any()) } returns "bar_token"
                    result = validator.validate(
                        TokenRuleRequest(
                            testUlid,
                            testToken
                        )
                    )
                }

                it("Should return Ok") {
                    result.shouldBeMessage<ServerMessage.NotAuthorized>()
                }
            }

            describe("When the client fails to connect") {
                beforeTest {
                    every { redisClient.connect() } throws RedisConnectionException("fail")
                    result = validator.validate(
                        TokenRuleRequest(
                            testUlid,
                            testToken
                        )
                    )
                }

                it("Should return Ok") {
                    result.shouldBeMessage<ServerMessage.ExternalError>()
                }
            }
        }
    }
})
