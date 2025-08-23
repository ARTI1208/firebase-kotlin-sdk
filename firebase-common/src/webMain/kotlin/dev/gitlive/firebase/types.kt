@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase

import dev.gitlive.firebase.externals.Object
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsNumber
import kotlin.js.JsReference
import kotlin.js.JsString
import kotlin.js.get
import kotlin.js.toDouble

public fun JsAny.toKotlin(): Any = when (this) {
    is JsString -> toString()
    is JsNumber -> toDouble()
    is JsArray<*> -> map { it?.toKotlin() }
    else -> runCatching<Any?> { (this as JsReference<*>).get() }.getOrNull() ?: Object.entries(this).toKotlin()
}