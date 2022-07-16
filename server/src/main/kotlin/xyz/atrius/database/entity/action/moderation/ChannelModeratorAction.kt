package xyz.atrius.database.entity.action.moderation

import org.hibernate.annotations.CreationTimestamp
import xyz.atrius.database.entity.channel.Channel
import xyz.atrius.database.entity.user.UserProfile
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "channel_moderator_action")
data class ChannelModeratorAction(

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: UserProfile?,

    @OneToOne
    @JoinColumn(name = "channel_id")
    val channel: Channel?,

    @Column(name = "action", nullable = false)
    val action: ModActionType,

    @Column(name = "reason")
    val reason: String?,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now()),

    @Column(name = "expiration_time")
    var expirationTime: Timestamp? = null
) : Serializable
