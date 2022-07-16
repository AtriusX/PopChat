package xyz.atrius.database.entity.user

import xyz.atrius.database.entity.channel.Channel
import xyz.atrius.database.entity.channel.ChannelStatus
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
    name = "user_channel",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "channel_id"])
    ]
)
data class UserChannel(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "channel_id")
    val channel: Channel,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: ChannelStatus = ChannelStatus.OPEN
) : Serializable
