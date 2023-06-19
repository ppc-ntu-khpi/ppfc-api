/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.model.service.subject.SubjectResponse

interface SubjectService {
    suspend fun add(subject: SubjectRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null
    ): List<SubjectResponse>
    suspend fun get(id: Long): SubjectResponse?
    suspend fun update(id: Long, subject: SubjectRequest)
    suspend fun delete(id: Long)
}