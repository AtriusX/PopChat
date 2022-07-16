package xyz.atrius.database.entity.message

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "message_attachment")
data class MessageAttachment(

    @Id
    @ManyToOne(cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "message_id")
    val message: Message,

    @Column(name = "data")
    val data: ByteArray
) : Serializable {

    override fun equals(other: Any?): Boolean =
        (other as? MessageAttachment)?.message == message

    override fun hashCode(): Int = message.hashCode()
}
