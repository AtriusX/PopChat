package xyz.atrius.dto.user

import xyz.atrius.dto.data.Dto
import xyz.atrius.dto.data.Tokenized

data class UserProfileDTO(
    override val token: String? = null,
    val displayName: String,
    val avatar: String? = null,
    val status: String? = null,
    val description: String? = null,
    val pronouns: String,
    val title: String? = null,
) : Tokenized, Dto