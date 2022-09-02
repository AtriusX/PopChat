package xyz.atrius.manager

import arrow.core.left
import arrow.core.right
import com.github.guepardoapps.kulid.ULID
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.types.shouldBeInstanceOf
import io.lettuce.core.RedisClient
import io.mockk.*
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserCredentials
import xyz.atrius.database.repository.UserCredentialsRepository
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.SpringDescribeSpec
import java.util.*

class AuthServiceSpec : SpringDescribeSpec({

    describe("AuthService tests") {

        lateinit var testCreds: UserCredentials
        lateinit var testUlid: ULIDIdentifier
        lateinit var testPassword: String
        lateinit var testToken: String
        lateinit var credentialsRepository: UserCredentialsRepository
        lateinit var passwordEncoder: Argon2PasswordEncoder
        lateinit var redisClient: RedisClient
        lateinit var service: AuthService

        beforeTest {
            testCreds = UserCredentials(
                id = "abc123",
                user = mockk(),
                passwordHash = "foo"
            )
            testUlid = ULID.random()
            testPassword = "password"
            testToken = "foo"
            credentialsRepository = mockk {
                every { findById(testUlid) } returns Optional.of(testCreds)
                every { save(any()) } returnsArgument 0
            }
            passwordEncoder = spyk(
                Argon2PasswordEncoder()
            ) {
                every { matches(any(), any()) } returns true
                every { encode(any()) } returns ""
            }
            redisClient = mockk {
                every { connect() } returns mockk {
                    every { sync() } returns mockk rc@{
                        every { this@rc.get(testUlid) } returns testToken
                    }
                }
                every { close() } just Runs
            }
            service = spyk(
                AuthService(
                    credentialsRepository,
                    passwordEncoder,
                    redisClient
                )
            ) {
                every { isAuthorized(testUlid, testToken) } answers {
                    println("TEST")
                    testUlid.right()
                }
            }
        }

        describe("isAuthorized") {
            lateinit var result: Message<ULIDIdentifier>

            beforeTest {
                clearMocks(service)
            }

            describe("A user is authorized") {
                beforeTest {
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result shouldBeRight testUlid
                }

                it("Should close the connection") {
                    verify(exactly = 1) {
                        redisClient.close()
                    }
                }
            }

            describe("When the provided identifier does not exist in the system") {
                beforeTest {
                    every { redisClient.connect().sync().get(testUlid) } returns null
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotFound>()
                }

                it("Should close the connection") {
                    verify(exactly = 1) {
                        redisClient.close()
                    }
                }
            }

            describe("When the provided token does not match the given ulid") {
                beforeTest {
                    every { redisClient.connect().sync().get(testUlid) } returns "oof"
                    result = service.isAuthorized(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotAuthorized>()
                }

                it("Should close the connection") {
                    verify(exactly = 1) {
                        redisClient.close()
                    }
                }
            }
        }

        describe("getCredentials") {
            lateinit var result: Message<UserCredentials>

            describe("A user's credentials are successfully retrieved") {
                beforeTest {
                    result = service.getCredentials(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result shouldBeRight testCreds
                }
            }

            describe("When the provided identifier does not exist in the cache") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotFound(ULIDIdentifier::class, testUlid).left()
                    result = service.getCredentials(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotFound>()
                }
            }

            describe("When a user is not authorized") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotAuthorized(testToken).left()
                    result = service.getCredentials(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotAuthorized>()
                }
            }

            describe("When the provided identifier does not exist in the database") {
                beforeTest {
                    every { credentialsRepository.findById(testUlid) } returns Optional.empty()
                    result = service.getCredentials(testUlid, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotFound>()
                }
            }
        }

        describe("isCorrectPassword") {
            lateinit var result: Message<ULIDIdentifier>

            describe("The provided password is validated successfully") {
                beforeTest {
                    result = service.isCorrectPassword(testUlid, testPassword, testToken)
                }

                it("Should return the correct result") {
                    result shouldBeRight testUlid
                }
            }

            describe("When the provided identifier does not exist in the cache") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotFound(ULIDIdentifier::class, testUlid).left()
                    result = service.isCorrectPassword(testUlid, testPassword, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotFound>()

                }
            }

            describe("When a user is not authorized") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotAuthorized(testToken).left()
                    result = service.isCorrectPassword(testUlid, testPassword, testToken)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotAuthorized>()

                }
            }

            describe("When the password is incorrect") {
                beforeTest {
                    every { passwordEncoder.matches(testPassword, testCreds.passwordHash) } returns false
                    result = service.isCorrectPassword(testUlid, testPassword, testToken)
                }

                it("Should return the correct result") {
                    result shouldBeLeft ServerMessage.WrongPassword
                }
            }
        }

        describe("updatePassword") {
            lateinit var result: Message<ServerMessage>

            val request = UserCredentialsUpdateRequest(
                token = "foo",
                newPassword = "bar",
                oldPassword = "foo"
            )

            describe("A password is updated successfully") {
                beforeTest {
                    result = service.updatePassword(testUlid, request)
                }

                it("Should return the correct result") {
                    result shouldBeRight ServerMessage.Ok
                }

                it("Should call encode") {
                    verify {
                        passwordEncoder.encode(request.newPassword)
                    }
                }

                it("Should call save") {
                    verify {
                        credentialsRepository.save(testCreds)
                    }
                }
            }

            describe("When the provided identifier does not exist in the cache") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotFound(ULIDIdentifier::class, testUlid).left()
                    result = service.updatePassword(testUlid, request)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotFound>()

                }
            }

            describe("When a user is not authorized") {
                beforeTest {
                    every {
                        service.isAuthorized(testUlid, testToken)
                    } returns ServerMessage.NotAuthorized(testToken).left()
                    result = service.updatePassword(testUlid, request)
                }

                it("Should return the correct result") {
                    result.shouldBeLeft().shouldBeInstanceOf<ServerMessage.NotAuthorized>()

                }
            }

            describe("When the password is incorrect") {
                beforeTest {
                    every { service.getCredentials(testUlid, testToken) } returns ServerMessage.WrongPassword.left()
                    result = service.updatePassword(testUlid, request)
                }

                it("Should return the correct result") {
                    result shouldBeLeft ServerMessage.WrongPassword
                }
            }
        }
    }
})
