package xyz.atrius.database.entity.admin

import org.hibernate.annotations.CreationTimestamp
import xyz.atrius.database.entity.user.UserProfile
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "admin")
data class Admin(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now())
) : Serializable