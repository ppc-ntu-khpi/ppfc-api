/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthNewPasswordRequiredChallengeRequest(
    val username: String,
    val password: String,
    val session: String
)
