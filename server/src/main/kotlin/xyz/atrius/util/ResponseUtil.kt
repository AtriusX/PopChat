package xyz.atrius.util

import arrow.core.Either
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import xyz.atrius.message.ServerMessage

typealias Response<T> = ResponseEntity<T>

typealias Message<T> = Either<ServerMessage, T>

fun <T> Either<ServerMessage, T>.asResponse(): Response<T> = fold(
    { ResponseEntity(HttpStatus.BAD_REQUEST) },
    { ResponseEntity(it, HttpStatus.OK) }
)