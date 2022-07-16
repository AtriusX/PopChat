package xyz.atrius.dto.user

import xyz.atrius.dto.data.Tokenized
import java.sql.Timestamp

data class UserProfileDTO(
    override val token: String? = null,
    val displayName: String? = null,
    val avatar: String? = null,
    val status: String? = null,
    val description: String? = null,
    val pronouns: String? = null,
    val title: String? = null,
    val creationTime: Timestamp? = null,
) : Tokenized