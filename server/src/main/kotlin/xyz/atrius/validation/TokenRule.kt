package xyz.atrius.validation

import arrow.core.continuations.either
import arrow.core.flatten
import io.lettuce.core.RedisClient
import org.springframework.stereotype.Component
import xyz.atrius.database.ULIDIdentifier
import xyz.atrius.message.ServerMessage
import xyz.atrius.util.*
import xyz.atrius.validation.TokenRule.TokenValidationRuleRequest

@Component
class TokenRule(
    private val redisClient: RedisClient,
) : ValidationRule<TokenValidationRuleRequest> {

    data class TokenValidationRuleRequest(
        val ulid: ULIDIdentifier,
        val token: String?,
    )

    override fun validate(
        input: TokenValidationRuleRequest,
    ): Message<ServerMessage.Ok> = either.eager {
        val (ulid, token) = input
        val cache = redisClient
            .connectSync {
                it.getOrFail(ulid) { ServerMessage.NotFound(ULIDIdentifier::class, ulid) }
            }
            .flatten()
            .bind()
        (token == cache)
            .rightIfTrue { ServerMessage.NotAuthorized(token) }
            .mapOk()
            .bind()
    }
}
