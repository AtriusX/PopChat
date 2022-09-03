package xyz.atrius.service.auth

import arrow.core.continuations.either
import arrow.core.left
import arrow.core.rightIfNotNull
import io.lettuce.core.RedisClient
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.message.ServerMessage
import xyz.atrius.service.gen.TokenGenService
import xyz.atrius.util.Message
import xyz.atrius.util.connectSync
import xyz.atrius.util.rightIfSuccess

@Service
class TokenService(
    private val redisClient: RedisClient,
    private val tokenGenService: TokenGenService
) {

    fun authorize(
        ulid: ULIDIdentifier
    ): Message<String> = either.eager {
        val token = tokenGenService.generateToken()
        redisClient.connectSync { cmd ->
            cmd.set(ulid, token)
                .rightIfSuccess {
                    ServerMessage.ExternalError(it)
                }
                .bind()
        }
        token
    }

    fun isAuthorized(
        ulid: ULIDIdentifier,
        token: String?,
    ): Message<ULIDIdentifier> = either.eager {
        val cache = redisClient.connectSync { cmd ->
            cmd.get(ulid)
                .rightIfNotNull {
                    ServerMessage.NotFound(ULIDIdentifier::class, ulid)
                }
                .bind()
        }
        if (cache != token) {
            ServerMessage.NotAuthorized(token)
                .left()
                .bind<ServerMessage>()
        }
        ulid
    }

    fun drop(
        ulid: ULIDIdentifier,
        token: String
    ): Message<ServerMessage> = either.eager {
        isAuthorized(ulid, token)
            .bind()
        redisClient.connectSync { cmd ->
            cmd.del(ulid)
        }
        ServerMessage.Ok
    }
}