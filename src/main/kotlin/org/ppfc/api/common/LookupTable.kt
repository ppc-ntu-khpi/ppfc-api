/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.common

import java.util.concurrent.ConcurrentHashMap

class LookupTable<K, V> {
    private val hashMap = ConcurrentHashMap<K, V?>()

    suspend fun getValue(key: K, block: suspend () -> V?): V? {
        return hashMap[key] ?: block().also {
            hashMap[key] = it
        }
    }

    fun clear() {
        hashMap.clear()
    }
}