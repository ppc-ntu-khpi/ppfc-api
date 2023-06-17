package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse

interface ChangeService {
    suspend fun add(change: ChangeRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        date: String? = null,
        isNumerator: Boolean? = null,
        groupId: Long? = null,
        groupNumber: Long? = null,
        teacherId: Long? = null
    ): List<ChangeResponse>
    suspend fun get(id: Long): ChangeResponse?
    suspend fun update(id: Long, change: ChangeRequest)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}