package dev.gitlive.firebase

import dev.gitlive.firebase.externals.Object
import kotlin.collections.component1
import kotlin.collections.component2

public actual object Object {
    public actual fun keys(any: Any): Set<String> {
        return Object.keys(any as JsAny).toList().mapTo(mutableSetOf()) {
            it.toString()
        }
    }

    public actual fun entries(json: Any?): List<Pair<String, Any?>> {
        val nativeEntries = Object.entries(json as JsAny)
        return nativeEntries.toList().map { arr ->
            val (key, value) = arr.toList()
            (key as JsString).toString() to value
        }
    }
}