/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.gitlive.firebase.internal

import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.toJsNumber
import kotlin.js.toJsString

public actual fun FirebaseEncoder.structureEncoder(descriptor: SerialDescriptor): FirebaseCompositeEncoder = when (descriptor.kind) {
    StructureKind.LIST -> encodeAsList(descriptor)
    StructureKind.MAP -> {
        val map = json()
        var lastKey = ""
        value = map
        FirebaseCompositeEncoder(settings) { _, index, value ->
            if (index % 2 == 0) {
                lastKey = (value as? String) ?: JSONAdapter.stringify(value)
            } else {
                map[lastKey] = value as JsAny?
            }
        }
    }
    StructureKind.CLASS, StructureKind.OBJECT -> encodeAsMap(descriptor)
    is PolymorphicKind -> encodeAsMap(descriptor)
    else -> TODO("The firebase-kotlin-sdk does not support $descriptor for serialization yet")
}

private fun FirebaseEncoder.encodeAsList(descriptor: SerialDescriptor): FirebaseCompositeEncoder = Array<Any?>(descriptor.elementsCount) { null }
    .also { value = it }
    .let { FirebaseCompositeEncoder(settings) { _, index, value -> it[index] = value } }
private fun FirebaseEncoder.encodeAsMap(descriptor: SerialDescriptor): FirebaseCompositeEncoder = json()
    .also { value = it }
    .let {
        FirebaseCompositeEncoder(
            settings,
            setPolymorphicType = { discriminator, type ->
                it[discriminator] = type as JsAny?
            },
            set = { _, index, value ->
                it[descriptor.getElementName(index)] = when (value) {
                    is String -> value.toJsString()
                    is Int -> value.toJsNumber()
                    is Long -> value.toInt().toJsNumber()
                    is Double -> value.toJsNumber()
                    is Float -> value.toDouble().toJsNumber()
                    else -> JSONAdapter.stringify(value as JsAny?).toJsString()
                }
            },
        )
    }
