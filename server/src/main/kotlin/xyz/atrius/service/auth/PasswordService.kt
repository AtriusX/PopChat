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
import xyz.atrius.util.*
import xyz.atrius.validation.PasswordRule

/**
 * This service is primarily geared towards the verification and modification of account passwords.
 * Unlike other components which use token validation, this is primarily used when no such token exists
 * yet, such as during the login/registration phase.
 *
 * It should be noted that this service does not do any validation against any external services aside
 * from the database on its own. It strictly looks at what is present there and makes decisions based
 * on that. As such, care should be taken where this service is used, to make sure calls to this component
 * are properly authorized when required.
 *
 * @property profileRepository
 *     Currently necessary for determining if credentials can be created for the given user.
 * @property credentialsRepository
 *     Used to set and retrieve credential data.
 * @property passwordEncoder
 *     Used to encode and validate passwords passed through the component. Passwords are **not** stored
 *     as plaintext values, and are instead encoded via Argon2.
 * @property passwordRule
 *     Used to determine if a provided password is valid for creation or replacement.
 */
@Service
class PasswordService(
    private val profileRepository: UserProfileRepository,
    private val credentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: Argon2PasswordEncoder,
    private val passwordRule: PasswordRule,
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
            .rightIfTrue { ServerMessage.WrongPassword }
            .mapOk()
            .bind()
    }

    fun create(
        ulid: ULIDIdentifier,
        password: String?,
    ): Message<ServerMessage> = either.eager {
        // Ensure the password follows the correct format
        passwordRule
            .validate(password)
            .bind()
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
        // Save the new credentials
        credentialsRepository.saveOk(
            UserCredentials(
                profile,
                passwordEncoder.encode(password)
            )
        )
    }

    fun update(
        ulid: ULIDIdentifier,
        newPassword: String,
    ): Message<ServerMessage> = either.eager {
        passwordRule
            .validate(newPassword)
            .bind()
        credentialsRepository.saveOk(
            getCreds(ulid).bind().apply {
                passwordHash = passwordEncoder.encode(newPassword)
            }
        )
    }

    private fun getCreds(ulid: ULIDIdentifier): Message<UserCredentials> = credentialsRepository
        .findByIdOrNull(ulid)
        .rightIfNotNull {
            ServerMessage.NotFound(UserCredentials::class, ulid)
        }
}
