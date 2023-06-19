/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshAccessTokenRequest(
    val refreshToken: String
)
