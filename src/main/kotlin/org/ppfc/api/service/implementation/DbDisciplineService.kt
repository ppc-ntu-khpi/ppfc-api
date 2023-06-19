/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.DisciplineService

class DbDisciplineService(private val database: Database) : DisciplineService {
    override suspend fun add(discipline: DisciplineRequest) = withContext(Dispatchers.IO) {
        database.disciplineQueries.insertModel(discipline.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?
    ) = withContext(Dispatchers.IO) {
        return@withContext database.disciplineQueries.selectWithParameters(
            offset = offset,
            limit = limit,
            searchQuery = searchQuery
        ).executeAsList().map { disciplineDto ->
            disciplineDto.toResponse()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        return@withContext database.disciplineQueries.selectWhereId(id = id).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun getByName(name: String) = withContext(Dispatchers.IO) {
        return@withContext database.disciplineQueries.selectWhereName(name = name).executeAsOneOrNull()?.toResponse()
    }

    override suspend fun update(id: Long, discipline: DisciplineRequest) = withContext(Dispatchers.IO) {
        database.disciplineQueries.updateWhereId(
            name = discipline.name,
            id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.disciplineQueries.deleteWhereId(id = id)
    }
}