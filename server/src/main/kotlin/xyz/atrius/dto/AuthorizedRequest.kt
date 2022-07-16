package xyz.atrius.dto

import xyz.atrius.dto.data.Tokenized

data class AuthorizedRequest(
    override val token: String?
) : Tokenized
