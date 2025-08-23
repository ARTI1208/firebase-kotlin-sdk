package dev.gitlive.firebase

import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.toList

public inline fun <T : JsAny?, R> JsArray<T>.map(transform: (T) -> R): List<R> =
    toList().map(transform)