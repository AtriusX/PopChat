package xyz.atrius.database.entity.user

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "user_block")
data class UserBlock(

    @Id
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val blockedUser: UserProfile
) : Serializable