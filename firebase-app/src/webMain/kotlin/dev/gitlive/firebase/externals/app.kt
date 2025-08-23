@file:OptIn(ExperimentalWasmJsInterop::class)
@file:JsModule("firebase/app")

package dev.gitlive.firebase.externals

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsModule
import kotlin.js.Promise
import kotlin.js.definedExternally

public external fun initializeApp(options: FirebaseOptions, name: String = definedExternally): FirebaseApp

public external fun getApp(name: String = definedExternally): FirebaseApp

public external fun getApps(): JsArray<FirebaseApp>

public external fun deleteApp(app: FirebaseApp): Promise<Nothing>

public external interface FirebaseApp : JsAny {
    public val automaticDataCollectionEnabled: Boolean
    public val name: String
    public val options: FirebaseOptions
}

public external interface FirebaseOptions : JsAny {
    public val apiKey: String
    public val appId: String
    public val authDomain: String?
    public val databaseURL: String?
    public val measurementId: String?
    public val messagingSenderId: String?
    public val gaTrackingId: String?
    public val projectId: String?
    public val storageBucket: String?
}
