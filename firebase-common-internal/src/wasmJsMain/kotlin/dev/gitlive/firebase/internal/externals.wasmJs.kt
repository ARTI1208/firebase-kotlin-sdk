package dev.gitlive.firebase.internal

import dev.gitlive.firebase.internal.externals.Date

public external object Object {

    public fun keys(obj: JsAny): JsArray<JsString>
    public fun entries(obj: JsAny): JsArray<JsArray<JsAny>>

    public fun fromEntries(obj: JsArray<JsArray<JsAny?>>): Json

}


public external object JSON {
    public fun stringify(o: JsAny?): String

    public fun parse(text: String): JsAny
}

public actual fun json(vararg pairs: Pair<String, Any?>): Json = Object.fromEntries(
    pairs.map {
        arrayOf(it.first.toJsString(), it.second as JsAny?).toJsArray()
    }.toJsArray()
)

private fun json(): Json = js("{}")

public actual object JsObject {
    public actual fun keys(json: Any?): Set<String> {
        val nativeKeys = Object.keys(json as JsAny)
        return nativeKeys.toList().mapTo(mutableSetOf()) {
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

public actual object JSONAdapter {
    public actual fun stringify(o: Any?): String {
        return JSON.stringify(o as JsAny?)
    }

    public actual fun <T> parse(text: String): T {
        return JSON.parse(text) as T
    }
}

public actual typealias Date = Date