package xyz.atrius.database.repository

import com.github.guepardoapps.kulid.ULID
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.util.SpringDescribeSpec
import xyz.atrius.util.beforeTestBlocking
import java.util.*
import javax.transaction.Transactional
import kotlin.properties.Delegates.notNull

@Transactional
@SpringBootTest
@Sql(scripts = ["classpath:/database/sql/user/user_profile_test_data.sql"])
class UserProfileRepositorySpec(
    private val repository: UserProfileRepository,
) : SpringDescribeSpec({

    describe("UserProfileRepository tests") {

        val testULID: ULIDIdentifier = ULID.fromString("01G844Z28PDXFE3M4AXSBK0FFR")
        val badULID: ULIDIdentifier = ULID.fromString("01G845Z28PDXFE3M4AXSBK0FFR")

        describe("findById") {
            lateinit var result: Optional<UserProfile>

            describe("User profile exists") {
                beforeTestBlocking {
                    result = repository.findById(testULID)
                }

                it("Should not be empty") {
                    result.shouldNotBeEmpty()
                }

                it("Should contain the correct result") {
                    with(result.get()) {
                        userId shouldBe testULID
                        displayName shouldBe "Test User A"
                        pronouns shouldBe "they/them"
                    }
                }
            }

            describe("When the given ulid does not match an existing profile") {
                beforeTestBlocking {
                    result = repository.findById(badULID)
                }

                it("Should be empty") {
                    result.shouldBeEmpty()
                }
            }
        }

        describe("existsById") {
            var result: Boolean by notNull()

            describe("Given id exists") {
                beforeTestBlocking {
                    result = repository.existsById(testULID)
                }

                it("Should exist") {
                    result.shouldBeTrue()
                }
            }

            describe("Given id doesn't exist") {
                beforeTestBlocking {
                    result = repository.existsById(badULID)
                }

                it("Should not exist") {
                    result.shouldBeFalse()
                }
            }
        }
    }
})