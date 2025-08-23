package dev.gitlive.firebase.auth

import dev.gitlive.firebase.auth.externals.*
import dev.gitlive.firebase.internal.Date
import dev.gitlive.firebase.internal.json
import dev.gitlive.firebase.map
import kotlinx.coroutines.jsAwait
import dev.gitlive.firebase.auth.externals.UserInfo as JsUserInfo

public actual class FirebaseUser internal constructor(public val js: User) {
    public actual val uid: String
        get() = rethrow { js.uid }
    public actual val displayName: String?
        get() = rethrow { js.displayName }
    public actual val email: String?
        get() = rethrow { js.email }
    public actual val phoneNumber: String?
        get() = rethrow { js.phoneNumber }
    public actual val photoURL: String?
        get() = rethrow { js.photoURL }
    public actual val isAnonymous: Boolean
        get() = rethrow { js.isAnonymous }
    public actual val isEmailVerified: Boolean
        get() = rethrow { js.emailVerified }
    public actual val metaData: UserMetaData?
        get() = rethrow { UserMetaData(js.metadata) }
    public actual val multiFactor: MultiFactor
        get() = rethrow { MultiFactor(multiFactor(js)) }
    public actual val providerData: List<UserInfo>
        get() = rethrow { js.providerData.map { UserInfo(it) } }
    public actual val providerId: String
        get() = rethrow { js.providerId }
    public actual suspend fun delete(): Unit = rethrow { js.delete().jsAwait() }
    public actual suspend fun reload(): Unit = rethrow { js.reload().jsAwait() }
    public actual suspend fun getIdToken(forceRefresh: Boolean): String? = rethrow { js.getIdToken(forceRefresh).jsAwait() }
    public actual suspend fun getIdTokenResult(forceRefresh: Boolean): AuthTokenResult = rethrow { AuthTokenResult(getIdTokenResult(js, forceRefresh).jsAwait()) }
    public actual suspend fun linkWithCredential(credential: AuthCredential): AuthResult = rethrow { AuthResult(linkWithCredential(js, credential.js).jsAwait()) }
    public actual suspend fun reauthenticate(credential: AuthCredential): Unit = rethrow {
        reauthenticateWithCredential(js, credential.js).jsAwait<Nothing>()
    }
    public actual suspend fun reauthenticateAndRetrieveData(credential: AuthCredential): AuthResult = rethrow { AuthResult(reauthenticateWithCredential(js, credential.js).jsAwait()) }

    public actual suspend fun sendEmailVerification(actionCodeSettings: ActionCodeSettings?): Unit = rethrow { sendEmailVerification(js, actionCodeSettings?.toJson()).jsAwait() }
    public actual suspend fun unlink(provider: String): FirebaseUser? = rethrow { FirebaseUser(unlink(js, provider).jsAwait()) }
    public actual suspend fun updateEmail(email: String): Unit = rethrow { updateEmail(js, email).jsAwait() }
    public actual suspend fun updatePassword(password: String): Unit = rethrow { updatePassword(js, password).jsAwait() }
    public actual suspend fun updatePhoneNumber(credential: PhoneAuthCredential): Unit = rethrow { updatePhoneNumber(js, credential.js).jsAwait() }
    public actual suspend fun updateProfile(displayName: String?, photoUrl: String?): Unit = rethrow {
        val request = listOf(
            "displayName" to displayName,
            "photoURL" to photoUrl,
        )
        updateProfile(js, json(*request.toTypedArray())).jsAwait()
    }
    public actual suspend fun verifyBeforeUpdateEmail(newEmail: String, actionCodeSettings: ActionCodeSettings?): Unit = rethrow { verifyBeforeUpdateEmail(js, newEmail, actionCodeSettings?.toJson()).jsAwait() }
}

public actual class UserInfo(public val js: JsUserInfo) {
    public actual val displayName: String?
        get() = rethrow { js.displayName }
    public actual val email: String?
        get() = rethrow { js.email }
    public actual val phoneNumber: String?
        get() = rethrow { js.phoneNumber }
    public actual val photoURL: String?
        get() = rethrow { js.photoURL }
    public actual val providerId: String
        get() = rethrow { js.providerId }
    public actual val uid: String
        get() = rethrow { js.uid }
}

public actual class UserMetaData(public val js: UserMetadata) {
    public actual val creationTime: Double?
        get() = rethrow { js.creationTime?.let { (Date(it).getTime() / 1000.0) } }
    public actual val lastSignInTime: Double?
        get() = rethrow { js.lastSignInTime?.let { (Date(it).getTime() / 1000.0) } }
}
