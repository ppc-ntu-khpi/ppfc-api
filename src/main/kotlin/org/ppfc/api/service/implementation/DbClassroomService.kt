package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.ClassroomService

class DbClassroomService(private val database: Database) : ClassroomService {
    override suspend fun add(classroom: ClassroomRequest) = withContext(Dispatchers.IO) {
        database.classroomQueries.insertModel(classroom.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?
    ) = withContext(Dispatchers.IO) {
        return@withContext database.classroomQueries.selectWithParameters(
            offset = offset,
            limit = limit,
            searchQuery = searchQuery
        ).executeAsList().map { classroomDto ->
            classroomDto.toResponse()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        return@withContext database.classroomQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun update(id: Long, classroom: ClassroomRequest) =
        withContext(Dispatchers.IO) {
            database.classroomQueries.updateWhereId(
                name = classroom.name,
                id = id
            )
        }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.classroomQueries.deleteWhereId(id = id)
    }
}