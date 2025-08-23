/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */

package dev.gitlive.firebase.firestore

import dev.gitlive.firebase.internal.JsObject
import dev.gitlive.firebase.internal.json

actual val emulatorHost: String = "localhost"

actual val context: Any = Unit

actual fun encodedAsMap(encoded: Any?): Map<String, Any?> = (JsObject.entries(encoded)).toMap()

actual fun Map<String, Any?>.asEncoded(): Any =
    json(*entries.map { (key, value) -> key to value }.toTypedArray())
