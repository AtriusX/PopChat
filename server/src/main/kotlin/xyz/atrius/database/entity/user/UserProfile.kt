package xyz.atrius.database.entity.user

import com.github.guepardoapps.kulid.ULID
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.dto.user.UserProfileDTO
import xyz.atrius.transfer.DtoTranslation
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user_profile")
data class UserProfile(

    @Id
    @Column(name = "user_id")
    val userId: ULIDIdentifier = ULID.random(),

    @Column(name = "display_name")
    var displayName: String = "",

    @Column(name = "avatar")
    var avatar: ByteArray? = null,

    @Column(name = "status")
    var status: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "pronouns", nullable = false)
    var pronouns: String = "they/them",

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "banned", nullable = false)
    var banned: Boolean = false,

    @CreationTimestamp
    @Column(name = "creation_time", insertable = false, updatable = false, nullable = false)
    val creationTime: Timestamp = Timestamp.from(Instant.now()),

    @UpdateTimestamp
    @Column(name = "update_time", insertable = false)
    var updateTime: Timestamp? = null,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false

) : DtoTranslation<UserProfileDTO>, Serializable {

    override fun asDto(): UserProfileDTO = UserProfileDTO(
        displayName = displayName,
        avatar = avatar?.let { String(it) },
        status = status,
        description = description,
        pronouns = pronouns,
        title = title
    )

    override fun fromDto(input: UserProfileDTO) {
        displayName = input.displayName
        avatar = input.avatar?.toByteArray()
        status = input.status
        description = input.description
        pronouns = input.pronouns
        title = input.title
    }

    override fun equals(other: Any?): Boolean =
        (other as? UserProfile)?.userId == userId

    override fun hashCode(): Int = userId.hashCode()
}