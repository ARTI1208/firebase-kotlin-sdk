/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase

import dev.gitlive.firebase.externals.createFirebaseOptions
import dev.gitlive.firebase.externals.deleteApp
import dev.gitlive.firebase.externals.getApp
import dev.gitlive.firebase.externals.getApps
import dev.gitlive.firebase.externals.initializeApp
import kotlinx.coroutines.jsAwait
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toList
import dev.gitlive.firebase.externals.FirebaseApp as JsFirebaseApp

public actual val Firebase.app: FirebaseApp
    get() = FirebaseApp(getApp())

public actual fun Firebase.app(name: String): FirebaseApp =
    FirebaseApp(getApp(name))

public actual fun Firebase.apps(context: Any?): List<FirebaseApp> {
    return getApps().toList().map { FirebaseApp(it) }
}

public actual fun Firebase.initialize(context: Any?): FirebaseApp? =
    throw UnsupportedOperationException("Cannot initialize firebase without options in JS")

public actual fun Firebase.initialize(context: Any?, options: FirebaseOptions, name: String): FirebaseApp =
    FirebaseApp(initializeApp(options.toJs(), name))

public actual fun Firebase.initialize(context: Any?, options: FirebaseOptions): FirebaseApp =
    FirebaseApp(initializeApp(options.toJs()))

private fun FirebaseOptions.toJs() = createFirebaseOptions(
    apiKey = apiKey,
    authDomain = authDomain,
    databaseURL = databaseUrl,
    projectId = projectId,
    storageBucket = storageBucket,
    messagingSenderId = gcmSenderId,
    appId = applicationId,
    measurementId = gaTrackingId,
)

public actual class FirebaseApp internal constructor(public val js: JsFirebaseApp) {

    public actual val name: String get() = js.name

    public actual val options: FirebaseOptions get() = js.options.run {
        FirebaseOptions(
            applicationId = appId,
            apiKey = apiKey,
            databaseUrl = databaseURL,
            gaTrackingId = gaTrackingId,
            storageBucket = storageBucket,
            projectId = projectId,
            gcmSenderId = messagingSenderId,
            authDomain = authDomain,
        )
    }

    public actual suspend fun delete() {
        deleteApp(js).jsAwait<Nothing>()
    }
}

public actual open class FirebaseException(code: String?, cause: Throwable) : Exception("$code: ${cause.message}", cause)
public actual open class FirebaseNetworkException(code: String?, cause: Throwable) : FirebaseException(code, cause)
public actual open class FirebaseTooManyRequestsException(code: String?, cause: Throwable) : FirebaseException(code, cause)
public actual open class FirebaseApiNotAvailableException(code: String?, cause: Throwable) : FirebaseException(code, cause)
