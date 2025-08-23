package dev.gitlive.firebase.auth

import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.internal.JSONAdapter

internal actual inline fun <R> rethrow(function: () -> R): R {
    try {
        return function()
    } catch (e: Exception) {
        throw e
    } catch (e: dynamic) {
        throw errorToException(e)
    }
}

private fun errorToException(cause: dynamic) = when (val code = cause.code?.toString()?.lowercase()) {
    "auth/invalid-user-token" -> FirebaseAuthInvalidUserException(code, cause.unsafeCast<Throwable>())
    "auth/requires-recent-login" -> FirebaseAuthRecentLoginRequiredException(code, cause.unsafeCast<Throwable>())
    "auth/user-disabled" -> FirebaseAuthInvalidUserException(code, cause.unsafeCast<Throwable>())
    "auth/user-token-expired" -> FirebaseAuthInvalidUserException(code, cause.unsafeCast<Throwable>())
    "auth/web-storage-unsupported" -> FirebaseAuthWebException(code, cause.unsafeCast<Throwable>())
    "auth/network-request-failed" -> FirebaseNetworkException(code, cause.unsafeCast<Throwable>())
    "auth/timeout" -> FirebaseNetworkException(code, cause.unsafeCast<Throwable>())
    "auth/weak-password" -> FirebaseAuthWeakPasswordException(code, cause.unsafeCast<Throwable>())
    "auth/invalid-credential",
    "auth/invalid-verification-code",
    "auth/missing-verification-code",
    "auth/invalid-verification-id",
    "auth/missing-verification-id",
        -> FirebaseAuthInvalidCredentialsException(code, cause.unsafeCast<Throwable>())
    "auth/maximum-second-factor-count-exceeded",
    "auth/second-factor-already-in-use",
        -> FirebaseAuthMultiFactorException(code, cause.unsafeCast<Throwable>())
    "auth/credential-already-in-use" -> FirebaseAuthUserCollisionException(code, cause.unsafeCast<Throwable>())
    "auth/email-already-in-use" -> FirebaseAuthUserCollisionException(code, cause.unsafeCast<Throwable>())
    "auth/invalid-email" -> FirebaseAuthEmailException(code, cause.unsafeCast<Throwable>())
//                "auth/app-deleted" ->
//                "auth/app-not-authorized" ->
//                "auth/argument-error" ->
//                "auth/invalid-api-key" ->
//                "auth/operation-not-allowed" ->
//                "auth/too-many-arguments" ->
//                "auth/unauthorized-domain" ->
    else -> {
        println("Unknown error code in ${JSONAdapter.stringify(cause)}")
        FirebaseAuthException(code, cause.unsafeCast<Throwable>())
    }
}
