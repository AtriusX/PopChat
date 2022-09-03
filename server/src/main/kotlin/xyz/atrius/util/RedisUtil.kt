package xyz.atrius.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands

inline fun <T> RedisClient.connectSync(
    block: (RedisCommands<String, String>) -> T
): T = connect().use {
    block(it.sync())
}

fun <T> String.rightIfSuccess(left: (String) -> T): Either<T, String> = when(this) {
    "OK" -> right()
    else -> left(this).left()
}