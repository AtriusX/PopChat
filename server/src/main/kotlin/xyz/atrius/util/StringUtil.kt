package xyz.atrius.util

import org.intellij.lang.annotations.Language

fun sql(@Language("SQL") input: String): String = input

fun regex(@Language("RegExp") input: String): String = input