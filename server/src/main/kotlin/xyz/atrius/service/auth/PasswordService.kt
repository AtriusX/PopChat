package xyz.atrius.service.auth

import arrow.core.continuations.either
import arrow.core.rightIfNotNull
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserCredentials
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.database.repository.UserCredentialsRepository
import xyz.atrius.database.repository.UserProfileRepository
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message
import xyz.atrius.util.findByIdOrNull
import xyz.atrius.util.rightIfNotExistsById
import xyz.atrius.util.rightIfTrue

@Service
class PasswordService(
    private val profileRepository: UserProfileRepository,
    private val credentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: Argon2PasswordEncoder,
) {

    fun isCorrect(
        ulid: ULIDIdentifier,
        password: String?,
    ): Message<ServerMessage> = either.eager {
        // Retrieve the credentials from the repository
        val creds = getCreds(ulid).bind()
        // Check is password matches
        passwordEncoder
            .matches(password, creds.passwordHash)
            .rightIfTrue {
                ServerMessage.WrongPassword
            }
            .bind()
        ServerMessage.Ok
    }

    fun create(
        ulid: ULIDIdentifier,
        password: String?,
    ): Message<ServerMessage> = either.eager {
        // Check if profile exists
        val profile = profileRepository
            .findByIdOrNull(ulid)
            .rightIfNotNull {
                ServerMessage.NotFound(UserProfile::class, ulid)
            }
            .bind()
        // Check if credentials have already been generated
        credentialsRepository
            .rightIfNotExistsById(ulid) {
                ServerMessage.AlreadyExists(UserCredentials::class, ulid)
            }
            .bind()
        credentialsRepository.save(
            UserCredentials(
                profile,
                passwordEncoder.encode(password)
            )
        )
        ServerMessage.Ok
    }

    fun update(
        ulid: ULIDIdentifier,
        newPassword: String,
    ): Message<ServerMessage> = getCreds(ulid)
        .map {
            it.apply { passwordHash = passwordEncoder.encode(newPassword) }
        }
        .tap(credentialsRepository::save)
        .map { ServerMessage.Ok }

    private fun getCreds(ulid: ULIDIdentifier): Message<UserCredentials> = credentialsRepository
        .findByIdOrNull(ulid)
        .rightIfNotNull {
            ServerMessage.AlreadyExists(UserCredentials::class, ulid)
        }
}