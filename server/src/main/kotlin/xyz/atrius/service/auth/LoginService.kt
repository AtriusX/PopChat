package xyz.atrius.service.auth

import arrow.core.continuations.either
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.database.repository.UserProfileRepository
import xyz.atrius.dto.user.UserProfileDTO
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message

/**
 * This service is primarily geared towards managing the initial authentication step of the application.
 * In contrast to [AuthService], which primarily is meant for ensuring authorization of already logged-in
 * users, this service focuses as the gateway for both new and returning users to gain entry to the platform.
 *
 * This service is not meant as a way to retrieve user profile data, it merely will determine if someone
 * is permitted to access the platform and issue them an authentication token that can be used in the
 * [AuthService] to later maintain authorization.
 *
 * @property profileRepository Used in the registration phase to generate new accounts.
 * @property passwordService Used to create and validate credentials. This keeps the validation logic out
 *                           of the gateway layer.
 *
 */
@Service
class LoginService(
    private val profileRepository: UserProfileRepository,
    private val passwordService: PasswordService,
    private val tokenService: TokenService,
) {

    fun login(
        ulid: ULIDIdentifier,
        password: String,
    ): Message<String> = either.eager {
        // Check that the password is correct
        passwordService
            .isCorrect(ulid, password)
            .bind()
        // Generate the session token
        tokenService
            .authorize(ulid)
            .bind()
    }

    fun register(
        password: String,
        profile: UserProfileDTO,
    ): Message<String> = either.eager {
        val user = UserProfile().apply {
            fromDto(profile)
        }
        profileRepository.save(user)
        passwordService.create(user.userId, password).bind()
        tokenService.authorize(user.userId).bind()
    }

    fun logout(
        ulid: ULIDIdentifier,
        token: String,
    ): Message<ServerMessage> =
        tokenService.drop(ulid, token)
}