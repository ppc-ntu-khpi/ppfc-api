/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse

interface TeacherService {
    suspend fun add(teacher: TeacherRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null,
        disciplineId: Long? = null,
        disciplineName: String? = null
    ): List<TeacherResponse>

    suspend fun get(id: Long): TeacherResponse?
    suspend fun getByFirstAndLastName(firstName: String, lastName: String): TeacherResponse?
    suspend fun update(id: Long, teacher: TeacherRequest)
    suspend fun delete(id: Long)
}