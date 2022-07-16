package xyz.atrius.database.entity.trait

import javax.persistence.*

@Entity
@Table(name = "trait")
data class Trait(

    @Id
    @Column(name = "trait_id")
    val traitId: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "description")
    val description: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "rarity")
    val rarity: TraitRarity = TraitRarity.COMMON
)