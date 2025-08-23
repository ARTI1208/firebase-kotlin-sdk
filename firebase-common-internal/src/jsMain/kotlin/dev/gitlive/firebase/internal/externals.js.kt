package dev.gitlive.firebase.internal

import dev.gitlive.firebase.map
import kotlin.js.Date
import kotlin.js.JSON
import kotlin.js.json

public actual fun json(vararg pairs: Pair<String, Any?>): Json {
    return json(pairs = pairs) as Json
}

public actual typealias JSONAdapter = JSON

public actual object JsObject {
    public actual fun keys(json: Any?): Set<String> {
        val nativeKeys: Array<String> = js("Object").keys(json)
        return nativeKeys.toSet()
    }

    public actual fun entries(json: Any?): List<Pair<String, Any?>> {
        val nativeEntries: Array<Array<Any>> = js("Object").entries(json)
        return nativeEntries.map { (key, value) ->
            (key as String) to value
        }
    }
}

public actual typealias Date = Date