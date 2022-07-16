package xyz.atrius.database.entity.user

import org.hibernate.annotations.CreationTimestamp
import xyz.atrius.database.entity.trait.Trait
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(
    name = "user_trait",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "trait_id"]
        )
    ]
)
data class UserTrait(
    @Id
    @ManyToOne(cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "user_id")
    val user: UserProfile,

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "trait_id")
    val trait: Trait,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: Timestamp = Timestamp.from(Instant.now()),
) : Serializable