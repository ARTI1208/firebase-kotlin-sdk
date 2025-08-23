package dev.gitlive.firebase.remoteconfig

internal actual inline fun <R> rethrow(function: () -> R): R {
    try {
        return function()
    } catch (e: Exception) {
        throw e
    }
}