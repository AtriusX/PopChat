package xyz.atrius.validation

import xyz.atrius.message.ServerMessage
import xyz.atrius.util.Message

fun interface ValidationRule<I> {

    fun validate(input: I): Message<ServerMessage.Ok>
}