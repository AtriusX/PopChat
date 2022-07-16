package xyz.atrius.dto.user

import xyz.atrius.dto.data.Tokenized

data class UserDeleteRequest(
    override val token: String?,
    val password: String?
) : Tokenized