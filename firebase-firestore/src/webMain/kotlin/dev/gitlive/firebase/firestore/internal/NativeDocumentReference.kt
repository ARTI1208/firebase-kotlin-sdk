@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase.firestore.internal

import dev.gitlive.firebase.firestore.EncodedFieldPath
import dev.gitlive.firebase.firestore.NativeCollectionReference
import dev.gitlive.firebase.firestore.NativeDocumentReferenceType
import dev.gitlive.firebase.firestore.NativeDocumentSnapshot
import dev.gitlive.firebase.firestore.Source
import dev.gitlive.firebase.firestore.errorToException
import dev.gitlive.firebase.firestore.externals.deleteDoc
import dev.gitlive.firebase.firestore.externals.getDoc
import dev.gitlive.firebase.firestore.externals.getDocFromCache
import dev.gitlive.firebase.firestore.externals.getDocFromServer
import dev.gitlive.firebase.firestore.externals.onSnapshot
import dev.gitlive.firebase.firestore.externals.refEqual
import dev.gitlive.firebase.firestore.externals.setDoc
import dev.gitlive.firebase.firestore.externals.updateDoc
import dev.gitlive.firebase.firestore.performUpdate
import dev.gitlive.firebase.firestore.rethrow
import dev.gitlive.firebase.internal.EncodedObject
import dev.gitlive.firebase.internal.js
import dev.gitlive.firebase.internal.json
import kotlinx.coroutines.jsAwait
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.toJsString

internal actual class NativeDocumentReference actual constructor(actual val nativeValue: NativeDocumentReferenceType) {
    val js: NativeDocumentReferenceType = nativeValue

    actual val id: String
        get() = rethrow { js.id }

    actual val path: String
        get() = rethrow { js.path }

    actual val parent: NativeCollectionReferenceWrapper
        get() = rethrow { NativeCollectionReferenceWrapper(js.parent) }

    actual fun collection(collectionPath: String) = rethrow {
        NativeCollectionReference(
            dev.gitlive.firebase.firestore.externals.collection(
                js,
                collectionPath,
            ),
        )
    }

    actual suspend fun get(source: Source) = rethrow {
        NativeDocumentSnapshot(
            js.get(source).jsAwait(),
        )
    }

    actual val snapshots: Flow<NativeDocumentSnapshot> get() = snapshots()

    actual fun snapshots(includeMetadataChanges: Boolean) = callbackFlow {
        val unsubscribe = onSnapshot(
            js,
            json("includeMetadataChanges" to includeMetadataChanges),
            { trySend(NativeDocumentSnapshot(it)) },
            { close(errorToException(it)) },
        )
        awaitClose { unsubscribe() }
    }

    actual suspend fun setEncoded(encodedData: EncodedObject, setOptions: SetOptions) = rethrow {
        setDoc(js, encodedData.js, setOptions.js).jsAwait<Unit>()
    }

    actual suspend fun updateEncoded(encodedData: EncodedObject) = rethrow {
        updateDoc(
            js,
            encodedData.js,
        ).jsAwait<Unit>()
    }

    actual suspend fun updateEncodedFieldsAndValues(encodedFieldsAndValues: List<Pair<String, Any?>>) {
        rethrow {
            encodedFieldsAndValues.takeUnless { encodedFieldsAndValues.isEmpty() }
                ?.performUpdate { field, value, moreFieldsAndValues ->
                    updateDoc(js, field, value as JsAny?, *(moreFieldsAndValues as Array<JsAny?>))
                }
                ?.jsAwait<Unit>()
        }
    }

    actual suspend fun updateEncodedFieldPathsAndValues(encodedFieldsAndValues: List<Pair<EncodedFieldPath, Any?>>) {
        rethrow {
            encodedFieldsAndValues.takeUnless { encodedFieldsAndValues.isEmpty() }
                ?.performUpdate { field, value, moreFieldsAndValues ->
                    updateDoc(js, field, value as JsAny?, *(moreFieldsAndValues as Array<JsAny?>))
                }?.jsAwait<Unit>()
        }
    }

    actual suspend fun delete() = rethrow { deleteDoc(js).jsAwait<Unit>() }

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is NativeDocumentReference &&
            refEqual(
                nativeValue,
                other.nativeValue,
            )
    override fun hashCode(): Int = nativeValue.hashCode()
    override fun toString(): String = "DocumentReference(path=$path)"

    private fun <R> List<Pair<String, Any?>>.performUpdate(
        update: (JsString, JsAny?, Array<JsAny?>) -> R,
    ) = update(
        this[0].first.toJsString(),
        this[0].second as JsAny?,
        this.drop(1).flatMap { (field, value) -> listOf(field.toJsString(), value as JsAny?) }.toTypedArray(),
    )
}

private fun NativeDocumentReferenceType.get(source: Source) = when (source) {
    Source.DEFAULT -> getDoc(this)
    Source.CACHE -> getDocFromCache(this)
    Source.SERVER -> getDocFromServer(this)
}
