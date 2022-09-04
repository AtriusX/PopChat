package xyz.atrius.validation

import arrow.core.continuations.either
import arrow.core.rightIfNotNull
import org.intellij.lang.annotations.Language
import org.springframework.stereotype.Component
import xyz.atrius.message.PasswordMessage
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.rightIfTrue

@Component
class PasswordRule : ValidationRule<String?> {

    override fun validate(input: String?): Message<ServerMessage.Ok> = either.eager {
        val password = input.notNull().bind()
        with(password) {
            sizeMatches(8, 20).bind()
            hasCapital().bind()
            hasDigit().bind()
            hasSpecial().bind()
        }
        ServerMessage.Ok
    }

    private fun String?.notNull(): Message<String> =
        rightIfNotNull { PasswordMessage.NoPasswordProvided }

    private fun String.sizeMatches(min: Int, max: Int): Message<Boolean> = (length in min..max)
        .rightIfTrue { PasswordMessage.ImproperSize(min, max) }

    private fun String.hasCapital(): Message<Boolean> =
        requiresMatch("[A-Z]", PasswordMessage.RequiresCapital)

    private fun String.hasDigit(): Message<Boolean> =
        requiresMatch("[0-9]", PasswordMessage.RequiresNumber)

    private fun String.hasSpecial(): Message<Boolean> =
        requiresMatch("[!@#$%^&*()]", PasswordMessage.RequiresSpecial)

    private fun String.requiresMatch(
        @Language("RegExp")
        regex: String,
        message: ServerMessage,
    ): Message<Boolean> =
        contains(regex.toRegex())
            .rightIfTrue { message }
}