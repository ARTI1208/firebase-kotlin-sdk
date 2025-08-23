package dev.gitlive.firebase.internal

import dev.gitlive.firebase.Object

public val EncodedObject.js: Json get() = json(*getRaw().entries.map { (key, value) -> key to value }.toTypedArray())

@PublishedApi
internal actual fun Any.asNativeMap(): Map<*, *>? {
    val json = when (this) {
        is Number -> null
        is Boolean -> null
        is String -> null
        is Map<*, *> -> {
            if (keys.all { it is String }) {
                json(*mapKeys { (key, _) -> key as String }.toList().toTypedArray())
            } else {
                null
            }
        }
        is Collection<*> -> null
        is Array<*> -> null
        else -> {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            this as Json
        }
    } ?: return null
    val mutableMap = mutableMapOf<String, Any?>()
    for (key in Object.keys(json)) {
        mutableMap[key] = json[key]
    }
    return mutableMap.toMap()
}
