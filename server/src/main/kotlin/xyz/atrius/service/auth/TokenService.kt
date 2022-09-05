package xyz.atrius.service.auth

import arrow.core.continuations.either
import arrow.core.flatten
import io.lettuce.core.RedisClient
import org.springframework.stereotype.Service
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.message.ServerMessage
import xyz.atrius.service.gen.TokenGenService
import xyz.atrius.util.Message
import xyz.atrius.util.connectSync
import xyz.atrius.util.mapOk
import xyz.atrius.util.setOrFail
import xyz.atrius.validation.TokenRule

@Service
class TokenService(
    private val redisClient: RedisClient,
    private val tokenGenService: TokenGenService,
    private val tokenValidationRule: TokenRule,
) {

    fun authorize(
        ulid: ULIDIdentifier,
    ): Message<String> = either.eager {
        val token = tokenGenService.generateToken()
        redisClient
            .connectSync { it.setOrFail(ulid, token, ServerMessage::ExternalError) }
            .flatten()
            .bind()
        token
    }

    fun isAuthorized(
        ulid: ULIDIdentifier,
        token: String?,
    ): Message<ULIDIdentifier> = tokenValidationRule
        .validate(
            TokenRule.TokenRuleRequest(
                ulid = ulid,
                token = token
            )
        )
        .map { ulid }

    fun drop(
        ulid: ULIDIdentifier,
        token: String,
    ): Message<ServerMessage> = either.eager {
        isAuthorized(ulid, token)
            .bind()
        redisClient.connectSync { cmd -> cmd.del(ulid) }
            .mapOk()
            .bind()
    }
}