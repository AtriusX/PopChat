package xyz.atrius.manager

import arrow.core.rightIfNotNull
import org.springframework.stereotype.Component
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.database.repository.UserProfileRepository
import xyz.atrius.dto.AuthorizedRequest
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.dto.user.UserDeleteRequest
import xyz.atrius.dto.user.UserProfileDTO
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message

@Component
class UserManager(
    private var profileRepository: UserProfileRepository,
) {

    fun getUser(ulid: ULIDIdentifier, body: AuthorizedRequest?): Message<UserProfile> = profileRepository
        .findById(ulid)
        .orElse(null)
        .rightIfNotNull { ServerMessage.NotFound(UserProfile::class) }

    fun updateUser(ulid: ULIDIdentifier, body: UserProfileDTO): ServerMessage {
        TODO()
    }

    fun updatePassword(ulid: ULIDIdentifier, body: UserCredentialsUpdateRequest): ServerMessage {
        TODO()
    }

    fun deleteUser(ulid: ULIDIdentifier, body: UserDeleteRequest): Message<ULIDIdentifier> {
        TODO()
    }
}