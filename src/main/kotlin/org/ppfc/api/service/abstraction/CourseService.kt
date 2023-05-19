package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.model.service.course.CourseResponse

interface CourseService {
    suspend fun add(course: CourseRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null
    ): List<CourseResponse>
    suspend fun get(id: Long): CourseResponse?
    suspend fun getByNumber(number: Long): CourseResponse?
    suspend fun update(id: Long, course: CourseRequest)
    suspend fun delete(id: Long)
}