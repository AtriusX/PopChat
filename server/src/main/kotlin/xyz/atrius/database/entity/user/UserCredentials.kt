package xyz.atrius.database.entity.user

import xyz.atrius.database.ULIDIdentifier
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "user_credentials")
data class UserCredentials(

    @Id
    @Column(name = "user_id")
    val id: ULIDIdentifier,

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @Column(name = "password_hash")
    var passwordHash: String
) : Serializable