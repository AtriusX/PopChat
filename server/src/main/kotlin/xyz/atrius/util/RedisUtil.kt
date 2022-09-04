package xyz.atrius.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands
import xyz.atrius.message.ServerMessage

typealias Commands =
        RedisCommands<String, String>

inline fun <T> RedisClient.connectSync(
    block: (Commands) -> T,
): Message<T> = runCatching { connect() }
    .getOrNull()
    .rightIfNotNull {
        ServerMessage.ExternalError("Could not contact the Redis service.")
    }
    .map {
        it.use { client -> block(client.sync()) }
    }

fun <T> String?.rightIfSuccess(
    left: (String) -> T
): Either<T, String> = when (this) {
    "OK" -> right()
    else -> left(this ?: "null").left()
}

fun <T> Commands.getOrFail(
    key: String,
    onFail: () -> T
): Either<T, String> =
    get(key).rightIfNotNull(onFail)

fun <T> Commands.setOrFail(
    key: String,
    value: String,
    onFail: (String) -> T,
): Either<T, String> =
    set(key, value).rightIfSuccess(onFail)