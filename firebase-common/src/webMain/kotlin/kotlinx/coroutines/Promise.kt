@file:OptIn(ExperimentalWasmJsInterop::class)

package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsException
import kotlin.js.JsPromiseError
import kotlin.js.Promise
import kotlin.js.thrownValue

/**
 * Awaits for completion of the promise without blocking.
 *
 * This suspending function is cancellable: if the [Job] of the current coroutine is cancelled while this
 * suspending function is waiting on the promise, this function immediately resumes with [CancellationException].
 * There is a **prompt cancellation guarantee**: even if this function is ready to return the result, but was cancelled
 * while suspended, [CancellationException] will be thrown. See [suspendCancellableCoroutine] for low-level details.
 */
@Suppress("UNCHECKED_CAST")
public suspend fun <T> Promise<JsAny?>.jsAwait(): T = suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    this@jsAwait.then(
        onFulfilled = { cont.resume(it as T); null },
        onRejected = { cont.resumeWithException(it.toThrowableOrNull() ?: error("Unexpected non-Kotlin exception $it")); null }
    )
}

/**
 * Awaits for completion of the promise without blocking.
 *
 * This suspending function is cancellable: if the [Job] of the current coroutine is cancelled while this
 * suspending function is waiting on the promise, this function immediately resumes with [CancellationException].
 * There is a **prompt cancellation guarantee**: even if this function is ready to return the result, but was cancelled
 * while suspended, [CancellationException] will be thrown. See [suspendCancellableCoroutine] for low-level details.
 */
@Suppress("UNCHECKED_CAST")
public suspend fun <T : JsAny?> Promise<T>.jsAwait2(): T = suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    this@jsAwait2.then(
        onFulfilled = { cont.resume(it as T); null },
        onRejected = { cont.resumeWithException(it.toThrowableOrNull() ?: error("Unexpected non-Kotlin exception $it")); null }
    )
}


public fun <T: JsAny?> CoroutineScope.promise(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Promise<T> =
    async(context, start, block).asPromise()

public fun <T : JsAny?> Deferred<T>.asPromise(): Promise<T> {
    val promise = Promise<T> { resolve, reject ->
        invokeOnCompletion {
            val e = getCompletionExceptionOrNull()
            if (e != null) {
                reject((e as JsException).thrownValue as JsPromiseError)
            } else {
                resolve(getCompleted())
            }
        }
    }
//    promise as
//    promise.asDynamic().deferred = this
    return promise
}
