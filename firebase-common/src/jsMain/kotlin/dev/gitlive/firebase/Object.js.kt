package dev.gitlive.firebase

public actual object Object {
    public actual fun keys(any: Any): Set<String> {
        val objectKeys = kotlin.js.js("Object.keys")
        return objectKeys(any).unsafeCast<Array<String>>().toSet()
    }

    public actual fun entries(json: Any?): List<Pair<String, Any?>> {
        val nativeEntries = kotlin.js.js("Object.entries")
        return nativeEntries.toList().map { arr: Array<Any> ->
            val (key, value) = arr.toList()
            key.toString() to value
        }
    }
}