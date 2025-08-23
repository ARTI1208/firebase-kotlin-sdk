@file:OptIn(ExperimentalWasmJsInterop::class)

package kotlinx.coroutines

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js


@Suppress("unused")
private fun jsThrow(e: JsAny) {
    js("throw e;")
}

//@Suppress("unused")
//private fun jsCatch(f: () -> Unit): JsAny? = js("""
//    let result = null;
//    try {
//        f();
//    } catch (e) {
//       result = e;
//    }
//    return result;
//    """)

/**
 * For a Dynamic value caught in JS, returns the corresponding [Throwable]
 * if it was thrown from Kotlin, or null otherwise.
 */
@ExperimentalWasmJsInterop
public fun JsAny.toThrowableOrNull(): Throwable? {
    val thisAny: Any = this
    if (thisAny is Throwable) return thisAny
    var result: Throwable? = Throwable("AAAAA")
//    jsCatch {
//        try {
//            jsThrow(this)
//        } catch (e: Throwable) {
//            result = e
//        }
//    }
    return result
}

//public expect fun JsAny.toThrowableOrNull(): Throwable?
