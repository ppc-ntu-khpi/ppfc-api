/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.accesskey.AccessKeyResponse
import kotlin.time.Duration

interface AccessKeyService {
    suspend fun generateAccessKey(expiration: Duration): AccessKeyResponse
    suspend fun verifyAccessKey(key: String): Boolean
}