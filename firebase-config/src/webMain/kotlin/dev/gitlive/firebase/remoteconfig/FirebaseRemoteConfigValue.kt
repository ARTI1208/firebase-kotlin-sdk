@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase.remoteconfig

import dev.gitlive.firebase.remoteconfig.externals.Value
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toDouble
import kotlin.js.toInt

public actual class FirebaseRemoteConfigValue(public val js: Value) {
    public actual fun asBoolean(): Boolean = rethrow { js.asBoolean() }
    public actual fun asByteArray(): ByteArray = rethrow { js.asString()?.encodeToByteArray() ?: byteArrayOf() }
    public actual fun asDouble(): Double = rethrow { js.asNumber().toDouble() }
    public actual fun asLong(): Long = rethrow { js.asNumber().toInt().toLong() }
    public actual fun asString(): String = rethrow { js.asString() ?: "" }
    public actual fun getSource(): ValueSource = rethrow { js.getSource().toSource() }

    private fun String.toSource() = when (this) {
        "default" -> ValueSource.Default
        "remote" -> ValueSource.Remote
        "static" -> ValueSource.Static
        else -> error("Unknown ValueSource: $this")
    }
}
