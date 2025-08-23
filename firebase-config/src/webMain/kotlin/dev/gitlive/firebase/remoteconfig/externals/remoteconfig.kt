@file:OptIn(ExperimentalWasmJsInterop::class)
@file:JsModule("firebase/remote-config")
@file:JsNonModule

package dev.gitlive.firebase.remoteconfig.externals

import dev.gitlive.firebase.JsNonModule
import dev.gitlive.firebase.externals.FirebaseApp
import dev.gitlive.firebase.internal.Json
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.JsModule
import kotlin.js.JsNumber
import kotlin.js.Promise
import kotlin.js.definedExternally

public external fun activate(remoteConfig: RemoteConfig): Promise<JsBoolean>

public external fun ensureInitialized(remoteConfig: RemoteConfig): Promise<Nothing>

public external fun fetchAndActivate(remoteConfig: RemoteConfig): Promise<JsBoolean>

public external fun fetchConfig(remoteConfig: RemoteConfig): Promise<Nothing>

public external fun getAll(remoteConfig: RemoteConfig): Json

public external fun getBoolean(remoteConfig: RemoteConfig, key: String): Boolean

public external fun getNumber(remoteConfig: RemoteConfig, key: String): JsNumber

public external fun getRemoteConfig(app: FirebaseApp? = definedExternally): RemoteConfig

public external fun getString(remoteConfig: RemoteConfig, key: String): String?

public external fun getValue(remoteConfig: RemoteConfig, key: String): Value

public external interface RemoteConfig : JsAny {
    public var defaultConfig: Json
    public var fetchTimeMillis: Double
    public var lastFetchStatus: String
    public val settings: Settings
}

public external interface Settings : JsAny {
    public var fetchTimeoutMillis: Int
    public var minimumFetchIntervalMillis: Int
}

public external interface Value : JsAny {
    public fun asBoolean(): Boolean

    public fun asNumber(): JsNumber
    public fun asString(): String?
    public fun getSource(): String
}
