package xyz.atrius.database.entity.channel

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "channel")
data class Channel(

    @Id
    @Column(name = "channel_id")
    val channelId: String,

    @Column(name = "name")
    var name: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "channel_type")
    var channelType: ChannelType = ChannelType.PRIVATE,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now()),

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: Timestamp? = null,

    @Column(name = "deleted")
    var deleted: Boolean = false
)
