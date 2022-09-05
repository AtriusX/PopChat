package xyz.atrius.service.gen

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldHaveLength
import org.springframework.boot.test.context.SpringBootTest
import xyz.atrius.util.shouldMatchUrlBase64

@SpringBootTest
class TokenGenServiceSpec(
    tokenGen: TokenGenService
) : DescribeSpec({

    describe("TokenGenService tests") {

        describe("generateToken") {
            lateinit var result: String

            describe("A token is generated with the default length") {
                beforeTest {
                    result = tokenGen.generateToken()
                }

                it("Should be the correct length") {
                    result shouldHaveLength 32
                }

                it("Should be formatted as base-64") {
                    result.shouldMatchUrlBase64()
                }
            }

            describe("A token is generated with the double length") {
                beforeTest {
                    result = tokenGen.generateToken(64)
                }

                it("Should be the correct length") {
                    result shouldHaveLength 64
                }

                it("Should be formatted as base-64") {
                    result.shouldMatchUrlBase64()
                }
            }

            describe("A token is generated with the 8x length") {
                beforeTest {
                    result = tokenGen.generateToken(256)
                }

                it("Should be the correct length") {
                    result shouldHaveLength 256
                }

                it("Should be formatted as base-64") {
                    result.shouldMatchUrlBase64()
                }
            }

            describe("When a token length of 0 is passed") {
                beforeTest {
                    result = tokenGen.generateToken(0)
                }

                it("Should be the correct length") {
                    result shouldHaveLength 0
                }

                it("Should be formatted as base-64") {
                    result.shouldMatchUrlBase64()
                }

                it("Should be empty") {
                    result.shouldBeEmpty()
                }
            }

            describe("When an awkward token length is passed") {
                beforeTest {
                    result = tokenGen.generateToken(13)
                }

                it("Should be the correct length, offset by byte trimming issues") {
                    result shouldHaveLength 12
                }

                it("Should be formatted as base-64") {
                    result.shouldMatchUrlBase64()
                }
            }
        }
    }
})
