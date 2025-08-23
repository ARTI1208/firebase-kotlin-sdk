package dev.gitlive.firebase.internal

import kotlinx.serialization.SerializationException

internal actual fun decodePlatformDouble(value: Any?) = when (value) {
    is Number -> value.toDouble()
    is String -> value.toDouble()
    else -> throw SerializationException("Expected $value (${value?.let { it::class.simpleName }}) to be double")
}