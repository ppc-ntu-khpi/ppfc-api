/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)
