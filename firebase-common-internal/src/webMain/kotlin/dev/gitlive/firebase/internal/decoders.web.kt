package dev.gitlive.firebase.internal

import kotlinx.serialization.SerializationException
import kotlin.js.JsAny
import kotlin.js.JsNumber
import kotlin.js.JsString
import kotlin.js.js
import kotlin.js.toDouble

internal actual fun decodePlatformDouble(value: Any?)= when (value) {
    is JsNumber -> value.toDouble()
    is JsString -> value.toString().toDouble()
    is Number -> value.toDouble()
    is String -> value.toDouble()
    else -> throw SerializationException("Expected $value (${value?.let { it::class.simpleName }} / ${typeOf(value as JsAny?)}) to be double")
}

private fun typeOf(e: JsAny?): String = ""