package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.bellschedule.BellScheduleRequest
import org.ppfc.api.model.service.bellschedule.BellScheduleResponse

interface BellScheduleService {
    suspend fun add(bellScheduleItem: BellScheduleRequest)
    suspend fun getAll(): List<BellScheduleResponse>
    suspend fun update(id: Long, bellScheduleItem: BellScheduleRequest)
    suspend fun delete(id: Long)
}