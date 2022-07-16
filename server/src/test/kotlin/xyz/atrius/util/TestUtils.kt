package xyz.atrius.util

import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

fun DescribeSpecContainerScope.beforeTestBlocking(block: () -> Unit) = beforeTest {
    block()
}