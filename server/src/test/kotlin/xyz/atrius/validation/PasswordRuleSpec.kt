package xyz.atrius.validation

import io.kotest.core.spec.style.DescribeSpec
import xyz.atrius.message.PasswordMessage
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.shouldBeMessage
import xyz.atrius.util.shouldBeOk

class PasswordRuleSpec : DescribeSpec({

    describe("PasswordRule tests") {

        lateinit var validator: PasswordRule

        beforeTest {
            validator = PasswordRule()
        }

        describe("validate") {
            lateinit var result: Message<ServerMessage.Ok>

            describe("A password is successfully validated") {
                beforeTest {
                    result = validator.validate("p4ssW0rd$")
                }

                it("Should return correctly") {
                    result.shouldBeOk()
                }
            }

            describe("When the password is null") {
                beforeTest {
                    result = validator.validate(null)
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.NoPasswordProvided>()
                }
            }

            describe("When the password is too long") {
                beforeTest {
                    result = validator.validate("loooooooooooooooooooong")
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.ImproperSize>()
                }
            }

            describe("When the password is too short") {
                beforeTest {
                    result = validator.validate("foo")
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.ImproperSize>()
                }
            }

            describe("When the password has no capital letters") {
                beforeTest {
                    result = validator.validate("abcdefghij")
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.RequiresCapital>()
                }
            }

            describe("When the password has no digits") {
                beforeTest {
                    result = validator.validate("AbcdefghiJ")
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.RequiresNumber>()
                }
            }

            describe("When the password has no special characters") {
                beforeTest {
                    result = validator.validate("AbcdefghiJ87")
                }

                it("Should return correctly") {
                    result.shouldBeMessage<PasswordMessage.RequiresSpecial>()
                }
            }
        }
    }
})
