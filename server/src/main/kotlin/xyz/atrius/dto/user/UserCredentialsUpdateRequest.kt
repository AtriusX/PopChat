package xyz.atrius.dto.user

import xyz.atrius.dto.data.Tokenized

data class UserCredentialsUpdateRequest(
    override val token: String? = null,
    val password: String? = null,
    val oldPassword: String? = null
) : Tokenized
