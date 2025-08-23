package dev.gitlive.firebase.internal

internal actual fun Json.hasKey(key: String): Boolean {
    return get(key) != undefined()
}

private fun undefined(): JsAny? = js("undefined")
