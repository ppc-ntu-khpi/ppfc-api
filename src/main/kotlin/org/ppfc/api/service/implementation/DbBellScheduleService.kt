package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.bellschedule.BellScheduleRequest
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.BellScheduleService

class DbBellScheduleService(private val database: Database) : BellScheduleService {
    override suspend fun add(bellScheduleItem: BellScheduleRequest) = withContext(Dispatchers.IO) {
        database.bellScheduleQueries.insertModel(bellScheduleItem.toDto())
    }

    override suspend fun getAll() = withContext(Dispatchers.IO) {
        return@withContext database.bellScheduleQueries.selectAll().executeAsList().map { it.toResponse() }
    }

    override suspend fun update(id: Long, bellScheduleItem: BellScheduleRequest) = withContext(Dispatchers.IO) {
        database.bellScheduleQueries.updateWhereId(
            id = id,
            lessonNumber = bellScheduleItem.lessonNumber,
            startTimeMillis = bellScheduleItem.startTimeMillis,
            endTimeMillis = bellScheduleItem.endTimeMillis
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.bellScheduleQueries.deleteWhereId(id = id)
    }
}