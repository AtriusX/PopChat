package xyz.atrius.database.entity.channel

import org.hibernate.annotations.CreationTimestamp
import xyz.atrius.database.entity.user.UserProfile
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(
    name = "channel_moderator",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["channel_id", "user_id"]
        )
    ]
)
data class ChannelModerator(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "channel_id")
    val channel: Channel,

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @CreationTimestamp
    @Column(name = "creation_timestamp")
    val creationTime: Timestamp = Timestamp.from(Instant.now())
) : Serializable