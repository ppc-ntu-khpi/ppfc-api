package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.course.CourseRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.CourseService

class DbCourseService(private val database: Database) : CourseService {
    override suspend fun add(course: CourseRequest) = withContext(Dispatchers.IO) {
        database.courseQueries.insertModel(course.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?
    ) = withContext(Dispatchers.IO) {
        return@withContext database.courseQueries.selectWithParameters(
            offset = offset,
            limit = limit,
            searchQuery = searchQuery
        ).executeAsList().map { courseDto ->
            courseDto.toResponse()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        return@withContext database.courseQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun getByNumber(number: Long) = withContext(Dispatchers.IO) {
        database.courseQueries.selectWhereNumber(number = number).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun update(id: Long, course: CourseRequest) = withContext(Dispatchers.IO) {
        database.courseQueries.updateWhereId(
            name = course.number,
            id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.courseQueries.deleteWhereId(id = id)
    }
}