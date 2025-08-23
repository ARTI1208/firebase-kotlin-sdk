package dev.gitlive.firebase.externals

import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsString

public external object Object {

    public fun keys(obj: JsAny): JsArray<JsString>

    public fun entries(obj: JsAny): JsArray<JsArray<JsAny>>

}