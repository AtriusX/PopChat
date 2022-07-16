package xyz.atrius.util

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringTestExtension

abstract class SpringDescribeSpec(body: DescribeSpec.() -> Unit = {}) : DescribeSpec(body) {

    override fun extensions(): List<Extension> =
        listOf(SpringTestExtension())
}