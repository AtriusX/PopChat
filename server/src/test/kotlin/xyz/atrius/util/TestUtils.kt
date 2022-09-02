package xyz.atrius.util

import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun DescribeSpecContainerScope.beforeTestBlocking(
    block: CoroutineScope.() -> Unit
) = beforeTest {
    withContext(Dispatchers.IO, block)
}