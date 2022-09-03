package xyz.atrius.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun <T> Boolean.rightIfTrue(left: () -> T): Either<T, Boolean> = when (this) {
    true -> right()
    else -> left().left()
}

fun <T> Boolean.rightIfNotTrue(left: () -> T): Either<T, Boolean> = when (this) {
    true -> left().left()
    else -> right()
}