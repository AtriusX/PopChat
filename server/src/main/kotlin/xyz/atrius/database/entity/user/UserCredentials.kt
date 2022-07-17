package xyz.atrius.database.entity.user

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "user_credentials")
data class UserCredentials(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @Column(name = "password_hash")
    var passwordHash: String
) : Serializable