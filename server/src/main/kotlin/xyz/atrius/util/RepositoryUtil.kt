package xyz.atrius.util

import arrow.core.Either
import org.springframework.data.repository.CrudRepository

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