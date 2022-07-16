package xyz.atrius.database.entity.message

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import xyz.atrius.database.entity.channel.Channel
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "message")
data class Message(

    @Id
    @Column(name = "message_id")
    val messageId: String,

    @ManyToOne(cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "channel_id")
    val channel: Channel,

    @Column(name = "text")
    var text: String,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now()),

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: Timestamp? = null,

    @Column(name = "deleted")
    var deleted: Boolean = false
)
