package dev.gitlive.firebase.firestore

import dev.gitlive.firebase.firestore.externals.FirestoreError
import dev.gitlive.firebase.internal.JSONAdapter
import kotlin.js.JsException
import kotlin.js.thrownValue

internal inline fun <R> rethrow(function: () -> R): R {
    try {
        return function()
    } catch (e: Exception) {
        throw e
    } catch (e: JsException) {
        throw errorToException(e)
    }
}

internal fun errorToException(e: JsException) = ((e.thrownValue as? FirestoreError)?.code ?: e.message ?: "").let {
    errorToException(it, e)
}

internal fun errorToException(e: FirestoreError) = errorToException(e.code, Throwable())

private fun errorToException(code: String, e: Throwable) = code
    .lowercase()
    .let {
        when {
            "cancelled" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.CANCELLED)
            "invalid-argument" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.INVALID_ARGUMENT)
            "deadline-exceeded" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.DEADLINE_EXCEEDED)
            "not-found" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.NOT_FOUND)
            "already-exists" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.ALREADY_EXISTS)
            "permission-denied" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.PERMISSION_DENIED)
            "resource-exhausted" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.RESOURCE_EXHAUSTED)
            "failed-precondition" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.FAILED_PRECONDITION)
            "aborted" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.ABORTED)
            "out-of-range" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.OUT_OF_RANGE)
            "unimplemented" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.UNIMPLEMENTED)
            "internal" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.INTERNAL)
            "unavailable" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.UNAVAILABLE)
            "data-loss" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.DATA_LOSS)
            "unauthenticated" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.UNAUTHENTICATED)
            "unknown" in it -> FirebaseFirestoreException(e, FirestoreExceptionCode.UNKNOWN)
            else -> {
                println("Unknown error code in ${JSONAdapter.stringify(e)}")
                FirebaseFirestoreException(e, FirestoreExceptionCode.UNKNOWN)
            }
        }
    }