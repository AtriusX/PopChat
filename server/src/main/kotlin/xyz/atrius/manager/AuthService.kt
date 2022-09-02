package xyz.atrius.manager

import arrow.core.continuations.either
import arrow.core.left
import arrow.core.rightIfNotNull
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserCredentials
import xyz.atrius.database.repository.UserCredentialsRepository
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message


@Service
class AuthService(
    private val credentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: Argon2PasswordEncoder
) {

    fun isAuthorized(ulid: ULIDIdentifier, token: String?): Message<ULIDIdentifier> =
        TODO("Auth service logic")

    fun getCredentials(
        ulid: ULIDIdentifier,
        token: String?
    ): Message<UserCredentials> = either.eager {
        isAuthorized(ulid, token)
            .bind()
        credentialsRepository
            .findById(ulid)
            .orElse(null)
            .rightIfNotNull {
                ServerMessage.NotFound(UserCredentials::class, ulid)
            }
            .bind()
    }

    fun isCorrectPassword(
        ulid: ULIDIdentifier,
        password: String?,
        token: String?
    ): Message<ULIDIdentifier> = either.eager {
        isAuthorized(ulid, token)
            .bind()
        val creds = getCredentials(ulid, token)
            .bind()
        if (!passwordEncoder.matches(password, creds.passwordHash)) {
            ServerMessage.WrongPassword
                .left()
                .bind<ServerMessage>()
        }
        ulid
    }

    fun updatePassword(
        ulid: ULIDIdentifier,
        body: UserCredentialsUpdateRequest
    ): Message<ServerMessage> = either.eager {
        val (token, newPassword, oldPassword) = body
        isAuthorized(ulid, token)
            .bind()
        isCorrectPassword(ulid, oldPassword, token)
            .bind()
        val creds = getCredentials(ulid, token)
            .bind()
        creds.passwordHash = passwordEncoder.encode(newPassword)
        credentialsRepository.save(creds)
        ServerMessage.Ok
    }
}