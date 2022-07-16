package xyz.atrius.database.entity.user

import xyz.atrius.database.entity.channel.Channel
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
    name = "user_connection",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "connected_user_id"])
    ]
)
data class UserConnection(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "connected_user_id")
    val connectedUser: UserProfile,

    @OneToOne
    @JoinColumn(name = "channel_id")
    var channel: Channel?
) : Serializable
