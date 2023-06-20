/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.accesskey

import kotlinx.serialization.Serializable

@Serializable
data class AccessKeyResponse(
    val key: String,
    val expiresAt: Long
)