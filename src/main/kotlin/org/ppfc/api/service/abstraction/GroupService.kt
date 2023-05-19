package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.group.GroupResponse

interface GroupService {
    suspend fun add(group: GroupRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null,
        courseId: Long? = null,
        courseNumber: Long? = null
    ): List<GroupResponse>

    suspend fun get(id: Long): GroupResponse?
    suspend fun getByNumber(number: Long): GroupResponse?
    suspend fun update(id: Long, group: GroupRequest)
    suspend fun delete(id: Long)
}