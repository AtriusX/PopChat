package xyz.atrius.service.auth

import arrow.core.continuations.either
import arrow.core.rightIfNotNull
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message


@Service
class AuthService(
    private val passwordService: PasswordService,
    private val tokenService: TokenService,
) {
    fun updatePassword(
        ulid: ULIDIdentifier,
        body: UserCredentialsUpdateRequest,
    ): Message<ServerMessage> = either.eager {
        val (token, newPassword, oldPassword) = body
        val new = newPassword
            .rightIfNotNull {
                ServerMessage.NullValue("new_password")
            }
            .bind()
        tokenService
            .isAuthorized(ulid, token)
            .bind()
        passwordService
            .isCorrect(ulid, oldPassword)
            .bind()
        passwordService.update(ulid, new)
        ServerMessage.Ok
    }
}