package xyz.atrius.database.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.database.entity.user.UserCredentials

@Repository
interface UserCredentialsRepository : CrudRepository<UserCredentials, ULIDIdentifier>