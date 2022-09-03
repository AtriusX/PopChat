package xyz.atrius.message

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable
import kotlin.reflect.KClass

sealed class ServerMessage(
    val message: String?,
    val error: Boolean = true,
) : Serializable {

    object Ok : ServerMessage(message = null, error = false)

    class NotFound(type: KClass<*>, input: Any?) : ServerMessage(
        message = "Could not find object of type ${type.simpleName}: No entry found for input $input."
    )

    class NotAuthorized(token: String?) : ServerMessage(
        message = "Could not fulfill request, token $token not authorized."
    )

    object WrongPassword : ServerMessage(
        message = "Incorrect password!"
    )

    class AlreadyExists(type: KClass<*>, input: Any?) : ServerMessage(
        message = "Item id '$input' of type ${type.simpleName} already exists and cannot be overwritten."
    )

    class ExternalError(error: String) : ServerMessage(
        message = "An error has been encountered in an external service: $error"
    )

    class NullValue(field: String) : ServerMessage(
        message = "Field '$field' must not be null."
    )

    fun asResponseEntity(): ResponseEntity<ServerMessage> = ResponseEntity(
        this, if (error) HttpStatus.BAD_REQUEST else HttpStatus.OK
    )
}
