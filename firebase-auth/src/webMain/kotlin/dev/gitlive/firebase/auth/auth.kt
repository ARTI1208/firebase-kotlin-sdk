/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */

package dev.gitlive.firebase.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.auth.externals.ActionCodeInfo
import dev.gitlive.firebase.auth.externals.Auth
import dev.gitlive.firebase.auth.externals.getAuth
import dev.gitlive.firebase.auth.externals.applyActionCode
import dev.gitlive.firebase.auth.externals.confirmPasswordReset
import dev.gitlive.firebase.auth.externals.createUserWithEmailAndPassword
import dev.gitlive.firebase.auth.externals.sendPasswordResetEmail
import dev.gitlive.firebase.auth.externals.fetchSignInMethodsForEmail
import dev.gitlive.firebase.auth.externals.sendSignInLinkToEmail
import dev.gitlive.firebase.auth.externals.isSignInWithEmailLink
import dev.gitlive.firebase.auth.externals.signInWithEmailAndPassword
import dev.gitlive.firebase.auth.externals.signInWithCustomToken
import dev.gitlive.firebase.auth.externals.signInAnonymously
import dev.gitlive.firebase.auth.externals.signInWithCredential
import dev.gitlive.firebase.auth.externals.signInWithEmailLink
import dev.gitlive.firebase.auth.externals.signOut
import dev.gitlive.firebase.auth.externals.updateCurrentUser
import dev.gitlive.firebase.auth.externals.verifyPasswordResetCode
import dev.gitlive.firebase.auth.externals.checkActionCode
import dev.gitlive.firebase.auth.externals.connectAuthEmulator
import dev.gitlive.firebase.auth.externals.IdTokenResult
import dev.gitlive.firebase.internal.JsObject
import dev.gitlive.firebase.internal.json
import dev.gitlive.firebase.map
import kotlinx.coroutines.jsAwait
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.JsArray
import kotlin.js.JsString
import dev.gitlive.firebase.auth.externals.AuthResult as JsAuthResult
import dev.gitlive.firebase.auth.externals.AdditionalUserInfo as JsAdditionalUserInfo

public actual val Firebase.auth: FirebaseAuth
    get() = rethrow { FirebaseAuth(getAuth()) }

public actual fun Firebase.auth(app: FirebaseApp): FirebaseAuth =
    rethrow { FirebaseAuth(getAuth(app.js)) }

public val FirebaseAuth.js: Auth get() = js

public actual class FirebaseAuth internal constructor(internal val js: Auth) {

    public actual val currentUser: FirebaseUser?
        get() = rethrow { js.currentUser?.let { FirebaseUser(it) } }

    public actual val authStateChanged: Flow<FirebaseUser?> get() = callbackFlow {
        val unsubscribe = js.onAuthStateChanged {
            trySend(it?.let { FirebaseUser(it) })
        }
        awaitClose { unsubscribe() }
    }

    public actual val idTokenChanged: Flow<FirebaseUser?> get() = callbackFlow {
        val unsubscribe = js.onIdTokenChanged {
            trySend(it?.let { FirebaseUser(it) })
        }
        awaitClose { unsubscribe() }
    }

    public actual var languageCode: String
        get() = js.languageCode ?: ""
        set(value) {
            js.languageCode = value
        }

    public actual suspend fun applyActionCode(code: String): Unit = rethrow { applyActionCode(js, code).jsAwait() }
    public actual suspend fun confirmPasswordReset(code: String, newPassword: String): Unit = rethrow { confirmPasswordReset(js, code, newPassword).jsAwait() }

    public actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult =
        rethrow { AuthResult(createUserWithEmailAndPassword(js, email, password).jsAwait()) }

    public actual suspend fun fetchSignInMethodsForEmail(email: String): List<String> = rethrow { fetchSignInMethodsForEmail(js, email).jsAwait<JsArray<JsString>>().map { it.toString() } }

    public actual suspend fun sendPasswordResetEmail(email: String, actionCodeSettings: ActionCodeSettings?): Unit =
        rethrow { sendPasswordResetEmail(js, email, actionCodeSettings?.toJson()).jsAwait() }

    public actual suspend fun sendSignInLinkToEmail(email: String, actionCodeSettings: ActionCodeSettings): Unit =
        rethrow { sendSignInLinkToEmail(js, email, actionCodeSettings.toJson()).jsAwait() }

    public actual fun isSignInWithEmailLink(link: String): Boolean = rethrow { isSignInWithEmailLink(js, link) }

    public actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult =
        rethrow { AuthResult(signInWithEmailAndPassword(js, email, password).jsAwait()) }

    public actual suspend fun signInWithCustomToken(token: String): AuthResult =
        rethrow { AuthResult(signInWithCustomToken(js, token).jsAwait()) }

    public actual suspend fun signInAnonymously(): AuthResult =
        rethrow { AuthResult(signInAnonymously(js).jsAwait()) }

    public actual suspend fun signInWithCredential(authCredential: AuthCredential): AuthResult =
        rethrow { AuthResult(signInWithCredential(js, authCredential.js).jsAwait()) }

    public actual suspend fun signInWithEmailLink(email: String, link: String): AuthResult =
        rethrow { AuthResult(signInWithEmailLink(js, email, link).jsAwait()) }

    public actual suspend fun signOut(): Unit = rethrow { signOut(js).jsAwait() }

    public actual suspend fun updateCurrentUser(user: FirebaseUser): Unit =
        rethrow { updateCurrentUser(js, user.js).jsAwait() }

    public actual suspend fun verifyPasswordResetCode(code: String): String =
        rethrow { verifyPasswordResetCode(js, code).jsAwait() }

    public actual suspend fun <T : ActionCodeResult> checkActionCode(code: String): T = rethrow {
        val result = checkActionCode(js, code).jsAwait<ActionCodeInfo>()
        @Suppress("UNCHECKED_CAST")
        return when (result.operation) {
            "EMAIL_SIGNIN" -> ActionCodeResult.SignInWithEmailLink
            "VERIFY_EMAIL" -> ActionCodeResult.VerifyEmail(result.data.email!!)
            "PASSWORD_RESET" -> ActionCodeResult.PasswordReset(result.data.email!!)
            "RECOVER_EMAIL" -> ActionCodeResult.RecoverEmail(result.data.email!!, result.data.previousEmail!!)
            "VERIFY_AND_CHANGE_EMAIL" -> ActionCodeResult.VerifyBeforeChangeEmail(
                result.data.email!!,
                result.data.previousEmail!!,
            )
            "REVERT_SECOND_FACTOR_ADDITION" -> ActionCodeResult.RevertSecondFactorAddition(
                result.data.email!!,
                result.data.multiFactorInfo?.let { MultiFactorInfo(it) },
            )
            else -> throw UnsupportedOperationException(result.operation)
        } as T
    }

    public actual fun useEmulator(host: String, port: Int): Unit = rethrow { connectAuthEmulator(js, "http://$host:$port") }
}

public val AuthResult.js: JsAuthResult get() = js

public actual class AuthResult(internal val js: JsAuthResult) {
    public actual val user: FirebaseUser?
        get() = rethrow { js.user?.let { FirebaseUser(it) } }
    public actual val credential: AuthCredential?
        get() = rethrow { js.credential?.let { AuthCredential(it) } }
    public actual val additionalUserInfo: AdditionalUserInfo?
        get() = rethrow { js.additionalUserInfo?.let { AdditionalUserInfo(it) } }
}

public val AdditionalUserInfo.js: JsAdditionalUserInfo get() = js

public actual class AdditionalUserInfo(
    internal val js: JsAdditionalUserInfo,
) {
    public actual val providerId: String?
        get() = js.providerId
    public actual val username: String?
        get() = js.username
    public actual val profile: Map<String, Any?>?
        get() = rethrow {
            val profile = js.profile ?: return@rethrow null
            return@rethrow JsObject.entries(profile).toMap()
        }
    public actual val isNewUser: Boolean
        get() = js.newUser
}

public val AuthTokenResult.js: IdTokenResult get() = js

public actual class AuthTokenResult(internal val js: IdTokenResult) {
//    actual val authTimestamp: Long
//        get() = js.authTime
    public actual val claims: Map<String, Any>
        get() = JsObject.keys(js.claims).mapNotNull { key ->
            js.claims[key]?.let { key to it }
        }.toMap()

//    actual val expirationTimestamp: Long
//        get() = android.expirationTime
//    actual val issuedAtTimestamp: Long
//        get() = js.issuedAtTime
    public actual val signInProvider: String?
        get() = js.signInProvider
    public actual val token: String?
        get() = js.token
}

internal fun ActionCodeSettings.toJson() = json(
    "url" to url,
    "android" to androidPackageName?.run { json("installApp" to installIfNotAvailable, "minimumVersion" to minimumVersion, "packageName" to packageName) },
    "dynamicLinkDomain" to dynamicLinkDomain,
    "handleCodeInApp" to canHandleCodeInApp,
    "ios" to iOSBundleId?.run { json("bundleId" to iOSBundleId) },
)

public actual open class FirebaseAuthException(code: String?, cause: Throwable) : FirebaseException(code, cause)
public actual open class FirebaseAuthActionCodeException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthEmailException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthInvalidCredentialsException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthWeakPasswordException(code: String?, cause: Throwable) : FirebaseAuthInvalidCredentialsException(code, cause)
public actual open class FirebaseAuthInvalidUserException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthMultiFactorException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthRecentLoginRequiredException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthUserCollisionException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)
public actual open class FirebaseAuthWebException(code: String?, cause: Throwable) : FirebaseAuthException(code, cause)

internal inline fun <T, R> T.rethrow(function: T.() -> R): R = dev.gitlive.firebase.auth.rethrow { function() }
