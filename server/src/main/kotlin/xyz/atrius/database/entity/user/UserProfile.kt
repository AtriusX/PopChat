package xyz.atrius.database.entity.user

import com.github.guepardoapps.kulid.ULID
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user_profile")
class UserProfile(

    @Id
    @Column(name = "user_id")
    val userId: String = ULID.random(),

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
    @Column(name = "update_time")
    var updateTime: Timestamp? = null,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false
) {
    override fun equals(other: Any?): Boolean =
        (other as? UserProfile)?.userId == userId

    override fun hashCode(): Int = userId.hashCode()
}