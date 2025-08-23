@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase.remoteconfig

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.Object
import dev.gitlive.firebase.internal.json
import dev.gitlive.firebase.remoteconfig.externals.*
import kotlinx.coroutines.jsAwait
import kotlinx.datetime.Instant
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsBoolean
import kotlin.js.toBoolean
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

public actual val Firebase.remoteConfig: FirebaseRemoteConfig
    get() = rethrow { FirebaseRemoteConfig(getRemoteConfig()) }

public actual fun Firebase.remoteConfig(app: FirebaseApp): FirebaseRemoteConfig = rethrow {
    FirebaseRemoteConfig(getRemoteConfig(app.js))
}

public actual class FirebaseRemoteConfig internal constructor(public val js: RemoteConfig) {
    public actual val all: Map<String, FirebaseRemoteConfigValue>
        get() = rethrow { getAllKeys().associateWith { getValue(it) } }

    public actual val info: FirebaseRemoteConfigInfo
        get() = rethrow {
            FirebaseRemoteConfigInfo(
                configSettings = js.settings.toFirebaseRemoteConfigSettings(),
                fetchTime = Instant.fromEpochMilliseconds(js.fetchTimeMillis.toLong()),
                lastFetchStatus = js.lastFetchStatus.toFetchStatus(),
            )
        }

    public actual suspend fun activate(): Boolean = rethrow {
        return activate(js).jsAwait()
    }

    public actual suspend fun ensureInitialized(): Unit = rethrow {
        ensureInitialized(js).jsAwait()
    }

    public actual suspend fun fetch(minimumFetchInterval: Duration?): Unit = rethrow {
        fetchConfig(js).jsAwait()
    }

    public actual suspend fun fetchAndActivate(): Boolean = rethrow {
        return fetchAndActivate(js).jsAwait<JsBoolean>().toBoolean()
    }

    public actual fun getValue(key: String): FirebaseRemoteConfigValue = rethrow {
        FirebaseRemoteConfigValue(getValue(js, key))
    }

    public actual fun getKeysByPrefix(prefix: String): Set<String> =
        getAllKeys().filter { it.startsWith(prefix) }.toSet()

    private fun getAllKeys(): Set<String> {
        return Object.keys(getAll(js))
    }

    public actual suspend fun reset() {
        // not implemented for JS target
    }

    public actual suspend fun settings(init: FirebaseRemoteConfigSettings.() -> Unit) {
        val settings = FirebaseRemoteConfigSettings().apply(init)
        js.settings.apply {
            fetchTimeoutMillis = settings.fetchTimeout.inWholeMilliseconds.toInt()
            minimumFetchIntervalMillis = settings.minimumFetchInterval.inWholeMilliseconds.toInt()
        }
    }

    public actual suspend fun setDefaults(vararg defaults: Pair<String, Any?>): Unit = rethrow {
        js.defaultConfig = json(*defaults)
    }

    private fun Settings.toFirebaseRemoteConfigSettings(): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings(
        fetchTimeout = fetchTimeoutMillis.toLong().milliseconds,
        minimumFetchInterval = minimumFetchIntervalMillis.toLong().milliseconds,
    )

    private fun String.toFetchStatus(): FetchStatus = when (this) {
        "no-fetch-yet" -> FetchStatus.NoFetchYet
        "success" -> FetchStatus.Success
        "failure" -> FetchStatus.Failure
        "throttle" -> FetchStatus.Throttled
        else -> error("Unknown FetchStatus: $this")
    }
}

public actual open class FirebaseRemoteConfigException(code: String, cause: Throwable) : FirebaseException(code, cause)

public actual class FirebaseRemoteConfigClientException(code: String, cause: Throwable) : FirebaseRemoteConfigException(code, cause)

public actual class FirebaseRemoteConfigFetchThrottledException(code: String, cause: Throwable) : FirebaseRemoteConfigException(code, cause)

public actual class FirebaseRemoteConfigServerException(code: String, cause: Throwable) : FirebaseRemoteConfigException(code, cause)
