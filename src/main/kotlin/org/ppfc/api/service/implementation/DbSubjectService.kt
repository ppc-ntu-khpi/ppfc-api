/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.SubjectService

class DbSubjectService(private val database: Database) : SubjectService {
    override suspend fun add(subject: SubjectRequest) = withContext(Dispatchers.IO) {
        database.subjectQueries.insertModel(subject.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?
    ) = withContext(Dispatchers.IO) {
        return@withContext database.subjectQueries.selectWithParameters(
            offset = offset,
            limit = limit,
            searchQuery = searchQuery
        ).executeAsList().map { subjectDto ->
            subjectDto.toResponse()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        return@withContext database.subjectQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun update(id: Long, subject: SubjectRequest) = withContext(Dispatchers.IO) {
        database.subjectQueries.updateWhereId(
            name = subject.name,
            id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.subjectQueries.deleteWhereId(id = id)
    }
}