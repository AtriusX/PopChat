package xyz.atrius.service.auth

import arrow.core.left
import arrow.core.right
import com.github.guepardoapps.kulid.ULID
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserCredentials
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.database.repository.UserCredentialsRepository
import xyz.atrius.database.repository.UserProfileRepository
import xyz.atrius.message.PasswordMessage
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.findByIdOrNull
import xyz.atrius.util.shouldBeMessage
import xyz.atrius.util.shouldBeOk
import xyz.atrius.validation.PasswordRule

class PasswordServiceSpec : DescribeSpec({

    describe("PasswordService tests") {

        lateinit var testUlid: ULIDIdentifier
        lateinit var testPassword: String
        lateinit var testProfile: UserProfile
        lateinit var profileRepository: UserProfileRepository
        lateinit var credentialsRepository: UserCredentialsRepository
        lateinit var passwordEncoder: Argon2PasswordEncoder
        lateinit var passwordRule: PasswordRule
        lateinit var service: PasswordService
        lateinit var result: Message<ServerMessage>

        beforeTest {
            testUlid = ULID.random()
            testPassword = "password"
            testProfile = UserProfile()
            profileRepository = mockk {
                every { findByIdOrNull(any()) } returns testProfile
            }
            credentialsRepository = mockk {
                every { findByIdOrNull(any()) } returns null
                every { existsById(any()) } returns false
                every { save(any()) } returnsArgument 0
            }
            passwordEncoder = mockk {
                every { encode(any()) } returns "foo"
                every { matches(any(), any()) } returns true
            }
            passwordRule = mockk {
                every { validate(any()) } returns ServerMessage.Ok.right()
            }
            service = PasswordService(
                profileRepository,
                credentialsRepository,
                passwordEncoder,
                passwordRule
            )
        }

        describe("isCorrect") {
            beforeTest {
                every { credentialsRepository.findByIdOrNull(any()) } returns UserCredentials(
                    testProfile,
                    testPassword
                )
            }

            describe("A password is successfully verified as correct") {
                beforeTest {
                    result = service.isCorrect(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeOk()
                }
            }

            describe("When the provided ulid does not match any existing credentials") {
                beforeTest {
                    every { credentialsRepository.findByIdOrNull(any()) } returns null
                    result = service.isCorrect(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.NotFound>()
                }
            }

            describe("When the given password does not match the saved hash") {
                beforeTest {
                    every { passwordEncoder.matches(any(), any()) } returns false
                    result = service.isCorrect(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.WrongPassword>()
                }
            }
        }

        describe("create") {

            describe("A new set of credentials is successfully created") {
                beforeTest {
                    result = service.create(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeOk()
                }

                it("Should save the credentials") {
                    verify(exactly = 1) {
                        credentialsRepository.save(any())
                    }
                }
            }

            describe("When the password does not follow the correct format") {
                beforeTest {
                    every { passwordRule.validate(any()) } returns PasswordMessage.RequiresCapital.left()
                    result = service.create(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.RequiresCapital>()
                }
            }

            describe("When a profile is not found for the given ULID") {
                beforeTest {
                    every { profileRepository.findByIdOrNull(any()) } returns null
                    result = service.create(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.NotFound>()
                }
            }

            describe("When a profile already has a set credentials entry") {
                beforeTest {
                    every { credentialsRepository.existsById(any()) } returns true
                    result = service.create(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<ServerMessage.AlreadyExists>()
                }
            }
        }

        describe("update") {
            beforeTest {
                every { credentialsRepository.findByIdOrNull(any()) } returns UserCredentials(
                    testProfile,
                    testPassword
                )
            }

            describe("A set of credentials is successfully updated") {
                beforeTest {
                    result = service.update(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeOk()
                }

                verify(exactly = 1) {
                    credentialsRepository.save(any())
                }
            }

            describe("When the password does not follow the correct format") {
                beforeTest {
                    every { passwordRule.validate(any()) } returns PasswordMessage.RequiresCapital.left()
                    result = service.update(testUlid, testPassword)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.RequiresCapital>()
                }
            }
        }
    }
})
