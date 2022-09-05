package xyz.atrius.util

import arrow.core.Either
import org.springframework.data.repository.CrudRepository
import xyz.atrius.message.ServerMessage

fun <T, ID> CrudRepository<T, ID>.findByIdOrNull(
    id: ID
): T? = findById(id)
    .orElse(null)

fun <T, ID, E> CrudRepository<T, ID>.rightIfExistsById(
    id: ID,
    left: () -> E
): Either<E, Boolean> = existsById(id)
    .rightIfTrue(left)

fun <T, ID, E> CrudRepository<T, ID>.rightIfNotExistsById(
    id: ID,
    left: () -> E
): Either<E, Boolean> = existsById(id)
    .rightIfNotTrue(left)

fun <T, ID> CrudRepository<T, ID>.saveOk(
    value: T
): ServerMessage.Ok = ServerMessage.Ok.also {
    save(value)
}