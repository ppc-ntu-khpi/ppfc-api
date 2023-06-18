package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.bellschedule.BellScheduleRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.BellScheduleService

class DbBellScheduleService(private val database: Database) : BellScheduleService {
    override suspend fun addAll(bellScheduleItems: List<BellScheduleRequest>) = withContext(Dispatchers.IO) {
        database.bellScheduleQueries.deleteAll()
        bellScheduleItems.forEach { item ->
            database.bellScheduleQueries.insertModel(item.toDto())
        }
    }

    override suspend fun getAll() = withContext(Dispatchers.IO) {
        return@withContext database.bellScheduleQueries.selectAll().executeAsList().map { it.toResponse() }
    }
}