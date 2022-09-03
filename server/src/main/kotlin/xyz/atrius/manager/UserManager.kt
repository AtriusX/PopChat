package xyz.atrius.manager

import arrow.core.continuations.either
import arrow.core.left
import arrow.core.rightIfNotNull
import org.springframework.stereotype.Component
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.database.repository.UserProfileRepository
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.dto.user.UserDeleteRequest
import xyz.atrius.dto.user.UserProfileDTO
import xyz.atrius.message.ServerMessage
import xyz.atrius.service.auth.AuthService
import xyz.atrius.service.auth.LoginService
import xyz.atrius.service.auth.PasswordService
import xyz.atrius.service.auth.TokenService
import xyz.atrius.util.Message

@Component
class UserManager(
    private val profileRepository: UserProfileRepository,
    private val authService: AuthService,
    private val loginService: LoginService,
    private val tokenService: TokenService,
    private val passwordService: PasswordService
) {

    fun login(
        username: String,
        password: String
    ): Message<String> = either.eager {
        val (userId) = getUserByUsername(username).bind()
        loginService.login(userId, password).bind()
    }

    fun logout(
        ulid: ULIDIdentifier,
        token: String
    ): Message<ServerMessage> =
        loginService.logout(ulid, token)

    fun getUser(
        ulid: ULIDIdentifier,
        token: String?
    ): Message<UserProfile> = either.eager {
        tokenService
            .isAuthorized(ulid, token)
            .bind()
        profileRepository
            .findById(ulid)
            .orElse(null)
            .rightIfNotNull { ServerMessage.NotFound(UserProfile::class, ulid) }
            .bind()
    }

    fun updateUser(
        ulid: ULIDIdentifier,
        body: UserProfileDTO
    ): Message<ServerMessage> = either.eager {
        val (token) = body
        tokenService
            .isAuthorized(ulid, token)
            .bind()
        getUser(ulid, token)
            .map {
                it.fromDto(body)
                profileRepository.save(it)
                ServerMessage.Ok
            }
            .bind()
    }

    fun updatePassword(
        ulid: ULIDIdentifier,
        body: UserCredentialsUpdateRequest
    ): Message<ServerMessage> = authService
        .updatePassword(ulid, body)

    fun deleteUser(
        ulid: ULIDIdentifier,
        body: UserDeleteRequest
    ): Message<ULIDIdentifier> = either.eager {
        val (token, password) = body
        if (!profileRepository.existsById(ulid)) {
            ServerMessage.NotFound(UserProfile::class, ulid)
                .left()
                .bind<ServerMessage>()
        }
        tokenService
            .isAuthorized(ulid, token)
            .bind()
        passwordService
            .isCorrect(ulid, password)
            .bind()
        profileRepository.deleteById(ulid)
        ulid
    }

    private fun getUserByUsername(
        username: String
    ): Message<UserProfile> = profileRepository
        .findByDisplayName(username)
        .rightIfNotNull {
            ServerMessage.NotFound(UserProfile::class, username)
        }
}
