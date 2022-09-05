package xyz.atrius.util

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import xyz.atrius.message.ServerMessage

fun <T> Boolean.rightIfTrue(left: () -> T): Either<T, Boolean> = when (this) {
    true -> right()
    else -> left().left()
}

fun <A> Either<A, Boolean>.rightIfTrue(left: () -> A): Either<A, Boolean> = getOrElse { false }
    .rightIfTrue(left)

fun <T> Boolean.rightIfNotTrue(left: () -> T): Either<T, Boolean> = when (this) {
    true -> left().left()
    else -> right()
}

fun <A, B> Either<A, B>.mapOk(block: (B) -> Unit = {}): Either<A, ServerMessage.Ok> =
    map {
        block(it)
        ServerMessage.Ok
    }
