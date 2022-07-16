package xyz.atrius.database.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserProfile

@Repository
interface UserProfileRepository : CrudRepository<UserProfile, ULIDIdentifier>