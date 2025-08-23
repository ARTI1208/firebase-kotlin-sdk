/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */

package dev.gitlive.firebase.internal

import dev.gitlive.firebase.Object
import dev.gitlive.firebase.toKotlin
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder

public actual fun FirebaseDecoder.structureDecoder(descriptor: SerialDescriptor, polymorphicIsNested: Boolean): CompositeDecoder = when (descriptor.kind) {
    StructureKind.CLASS, StructureKind.OBJECT -> decodeAsMap(false)
    StructureKind.LIST -> decodeAsList()
    StructureKind.MAP -> (Object.entries(value)).let {
        FirebaseCompositeDecoder(
            it.size,
            settings,
        ) { desc, index ->
            it[index / 2].run {
                if (index % 2 == 0) {
                    val key = first
                    if (desc.getElementDescriptor(index).kind == PrimitiveKind.STRING) {
                        key
                    } else {
                        JSONAdapter.parse<Any?>(key)
                    }
                } else {
                    second
                }
            }
        }
    }

    is PolymorphicKind -> decodeAsMap(polymorphicIsNested)
    else -> TODO("The firebase-kotlin-sdk does not support $descriptor for serialization yet")
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
public actual fun getPolymorphicType(value: Any?, discriminator: String): String =
    (value as Json)[discriminator]?.toKotlin() as String

private fun FirebaseDecoder.decodeAsList(): CompositeDecoder = (value as Array<*>).let {
    FirebaseCompositeDecoder(it.size, settings) { _, index -> it[index] }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
private fun FirebaseDecoder.decodeAsMap(isNestedPolymorphic: Boolean): CompositeDecoder = (value as Json).let { json ->
    FirebaseClassDecoder(Object.keys(value).size, settings, { json.hasKey(it) }) { desc, index ->
        if (isNestedPolymorphic) {
            if (desc.getElementName(index) == "value") {
                json?.toKotlin()
            } else {
                json[desc.getElementName(index)]?.toKotlin()
            }
        } else {
            json[desc.getElementName(index)]?.toKotlin()
        }
    }
}

internal expect fun Json.hasKey(key: String): Boolean

