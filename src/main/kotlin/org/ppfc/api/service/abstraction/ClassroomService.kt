/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.model.service.classroom.ClassroomResponse

interface ClassroomService {
    suspend fun add(classroom: ClassroomRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null
    ): List<ClassroomResponse>
    suspend fun get(id: Long): ClassroomResponse?
    suspend fun update(id: Long, classroom: ClassroomRequest)
    suspend fun delete(id: Long)
}