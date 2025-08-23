package dev.gitlive.firebase.internal

internal actual fun Json.hasKey(key: String): Boolean {
    return get(key) != undefined
}