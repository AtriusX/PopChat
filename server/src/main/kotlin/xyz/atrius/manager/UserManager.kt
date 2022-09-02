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
import xyz.atrius.service.AuthService
import xyz.atrius.util.Message

@Component
class UserManager(
    private val profileRepository: UserProfileRepository,
    private val authService: AuthService,
) {

    fun getUser(
        ulid: ULIDIdentifier,
        token: String?
    ): Message<UserProfile> = either.eager {
        authService
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
        authService
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
        val delete = authService
            .isCorrectPassword(ulid, password, token)
            .bind()
        profileRepository.deleteById(delete)
        ulid
    }
}
