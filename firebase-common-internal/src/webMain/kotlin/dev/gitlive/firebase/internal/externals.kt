package dev.gitlive.firebase.internal

import kotlin.js.JsAny

public external interface Json : JsAny {

    public operator fun get(propertyName: String): JsAny?
    public operator fun set(propertyName: String, value: JsAny?)
}

public expect fun json(vararg pairs: Pair<String, Any?>): Json

public expect object JsObject {
    public fun keys(json: Any?): Set<String>
    public fun entries(json: Any?): List<Pair<String, Any?>>
}

public expect object JSONAdapter {

    public fun stringify(o: Any?): String
    public fun <T> parse(text: String): T

}

public expect class Date(dateString: String) {

    public fun getDate(): Int

    public fun getTime(): Double

}
