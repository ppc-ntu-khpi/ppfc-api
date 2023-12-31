/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.security.auth

sealed class RefreshAccessTokenResult {
    class Success(val accessToken: String) : RefreshAccessTokenResult()
    class Failure(val error: AuthError) : RefreshAccessTokenResult()
}