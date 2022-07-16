package xyz.atrius.database.entity.user

import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*

@Entity
@Table(name = "user_session")
data class UserSession(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @Column(name = "token")
    val token: String,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now()),

    @Column(name = "expiration_time")
    var expirationTime: Timestamp? = Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS))
) : Serializable
