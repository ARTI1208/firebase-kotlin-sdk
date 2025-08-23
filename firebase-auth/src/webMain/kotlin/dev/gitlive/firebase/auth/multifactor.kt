package dev.gitlive.firebase.auth

import dev.gitlive.firebase.auth.externals.MultiFactorUser
import dev.gitlive.firebase.internal.Date
import dev.gitlive.firebase.map
import kotlinx.coroutines.jsAwait
import dev.gitlive.firebase.auth.externals.MultiFactorAssertion as JsMultiFactorAssertion
import dev.gitlive.firebase.auth.externals.MultiFactorInfo as JsMultiFactorInfo
import dev.gitlive.firebase.auth.externals.MultiFactorResolver as JsMultiFactorResolver
import dev.gitlive.firebase.auth.externals.MultiFactorSession as JsMultiFactorSession

public actual class MultiFactor(public val js: MultiFactorUser) {
    public actual val enrolledFactors: List<MultiFactorInfo>
        get() = rethrow { js.enrolledFactors.map { MultiFactorInfo(it) } }
    public actual suspend fun enroll(multiFactorAssertion: MultiFactorAssertion, displayName: String?): Unit =
        rethrow { js.enroll(multiFactorAssertion.js, displayName).jsAwait() }
    public actual suspend fun getSession(): MultiFactorSession =
        rethrow { MultiFactorSession(js.getSession().jsAwait()) }
    public actual suspend fun unenroll(multiFactorInfo: MultiFactorInfo): Unit =
        rethrow { js.unenroll(multiFactorInfo.js).jsAwait() }
    public actual suspend fun unenroll(factorUid: String): Unit =
        rethrow { js.unenroll(factorUid).jsAwait() }
}

public actual class MultiFactorInfo(public val js: JsMultiFactorInfo) {
    public actual val displayName: String?
        get() = rethrow { js.displayName }
    public actual val enrollmentTime: Double
        get() = rethrow { (Date(js.enrollmentTime).getTime() / 1000.0) }
    public actual val factorId: String
        get() = rethrow { js.factorId }
    public actual val uid: String
        get() = rethrow { js.uid }
}

public actual class MultiFactorAssertion(public val js: JsMultiFactorAssertion) {
    public actual val factorId: String
        get() = rethrow { js.factorId }
}

public actual class MultiFactorSession(public val js: JsMultiFactorSession)

public actual class MultiFactorResolver(public val js: JsMultiFactorResolver) {
    public actual val auth: FirebaseAuth = rethrow { FirebaseAuth(js.auth) }
    public actual val hints: List<MultiFactorInfo> = rethrow { js.hints.map { MultiFactorInfo(it) } }
    public actual val session: MultiFactorSession = rethrow { MultiFactorSession(js.session) }

    public actual suspend fun resolveSignIn(assertion: MultiFactorAssertion): AuthResult = rethrow { AuthResult(js.resolveSignIn(assertion.js).jsAwait()) }
}
