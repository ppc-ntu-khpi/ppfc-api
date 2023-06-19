/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.model.service.user.UserResponse

interface UserService {
    suspend fun add(user: UserRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null,
        isStudent: Boolean? = null
    ): List<UserResponse>
    suspend fun get(id: Long): UserResponse?
    suspend fun update(user: UserRequest)
    suspend fun delete(id: Long)
}