package xyz.atrius.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile
import xyz.atrius.dto.AuthorizedRequest
import xyz.atrius.dto.user.UserCredentialsUpdateRequest
import xyz.atrius.dto.user.UserDeleteRequest
import xyz.atrius.dto.user.UserProfileDTO
import xyz.atrius.manager.UserManager
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Response
import xyz.atrius.util.asResponse
import xyz.atrius.util.mergeAsResponse

@RestController
class UserController(
    private val userManager: UserManager,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserController::class.java)
    }

    /**
     * Gets a given user from the application. Returns the User object containing the requested
     * user’s data if successful, or a ServerMessage if an error is encountered.
     */
    @GetMapping("/user/{user}")
    fun getUser(
        @PathVariable user: ULIDIdentifier,
        @RequestBody body: AuthorizedRequest
    ): Response<UserProfileDTO> = userManager
        .getUser(user, body.token)
        .map(UserProfile::asDto)
        .tapLeft { logger.error(it.message) }
        .asResponse()

    /**
     * Updates a user’s profile information. Returns a User object containing the updated
     * information if successful, or a ServerMessage if an error is encountered.
     */
    @PostMapping("/user/{user}")
    fun updateUser(
        @PathVariable user: ULIDIdentifier,
        @RequestBody body: UserProfileDTO,
    ): Response<ServerMessage> = userManager
        .updateUser(user, body)
        .mergeAsResponse()

    /**
     * Updates a given user’s password. Returns a ServerMessage if the update was successful
     * or if an error is encountered.
     */
    @PostMapping("/user/{user}/password")
    fun updateUserPassword(
        @PathVariable user: ULIDIdentifier,
        @RequestBody body: UserCredentialsUpdateRequest
    ): Response<ServerMessage> = userManager
        .updatePassword(user, body)
        .mergeAsResponse()

    /**
     * Deletes a user account completely from the application. Returns the ULID of the deleted
     * account if successful, or a ServerMessage if an error is encountered.
     */
    @DeleteMapping("/user/{user}")
    fun deleteUser(
        @PathVariable user: ULIDIdentifier,
        @RequestBody body: UserDeleteRequest
    ): Response<ULIDIdentifier> = userManager
        .deleteUser(user, body)
        .asResponse()
}