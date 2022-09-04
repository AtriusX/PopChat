package xyz.atrius.util

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import io.kotest.matchers.types.shouldBeInstanceOf
import io.lettuce.core.RedisClient
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.atrius.message.ServerMessage

fun DescribeSpecContainerScope.beforeTestBlocking(
    block: CoroutineScope.() -> Unit,
) = beforeTest {
    withContext(Dispatchers.IO, block)
}

fun mockkRedisClient(mock: RedisClient.() -> Unit = {}): RedisClient {
    return mockk(relaxUnitFun = true) {
        every { connect() } returns mockk(relaxUnitFun = true) {
            every { sync() } returns mockk()
        }
        mock(this)
    }
}

fun Message<ServerMessage.Ok>.shouldBeOk(): ServerMessage.Ok = shouldBeRight()

inline fun <reified T : ServerMessage> Message<ServerMessage.Ok>.shouldBeMessage() =
    shouldBeLeft().shouldBeInstanceOf<T>()