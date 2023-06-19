/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.LookupTable
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.course.CourseResponse
import org.ppfc.api.model.service.group.GroupRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.CourseService
import org.ppfc.api.service.abstraction.GroupService

class DbGroupService(private val database: Database) : GroupService, KoinComponent {
    private val courseService: CourseService by inject()

    override suspend fun add(group: GroupRequest) = withContext(Dispatchers.IO) {
        database.groupQueries.insertModel(group.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?,
        courseId: Long?,
        courseNumber: Long?
    ) = withContext(Dispatchers.IO) {
        val coursesLookupTable = LookupTable<Long, CourseResponse>()

        val courseIdParam = courseId ?: courseNumber?.let {
            courseService.getByNumber(courseNumber)?.id ?: 0L
        }

        return@withContext try {
            database.groupQueries.selectWithParameters(
                offset = offset,
                limit = limit,
                searchQuery = searchQuery,
                courseId = courseIdParam
            ).executeAsList().mapNotNull { groupDto ->
                val course = coursesLookupTable.getValue(groupDto.courseId) {
                    courseService.get(groupDto.courseId)
                } ?: return@mapNotNull null

                groupDto.toResponse(course = course)
            }
        } finally {
            coursesLookupTable.clear()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        val group = database.groupQueries.selectWhereId(id = id).executeAsOneOrNull()
            ?: return@withContext null

        val course = courseService.get(group.courseId) ?: return@withContext null

        return@withContext group.toResponse(course = course)
    }

    override suspend fun getByNumber(number: Long) = withContext(Dispatchers.IO) {
        val group = database.groupQueries.selectWhereNumber(number = number).executeAsOneOrNull()
            ?: return@withContext null

        val course = courseService.get(group.courseId) ?: return@withContext null

        return@withContext group.toResponse(course = course)
    }

    override suspend fun update(id: Long, group: GroupRequest) = withContext(Dispatchers.IO) {
        database.groupQueries.updateWhereId(
            number = group.number, courseId = group.courseId, id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.groupQueries.deleteWhereId(id = id)
    }
}