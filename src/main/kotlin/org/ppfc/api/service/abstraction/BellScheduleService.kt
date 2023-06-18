package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.bellschedule.BellScheduleRequest
import org.ppfc.api.model.service.bellschedule.BellScheduleResponse

interface BellScheduleService {
    suspend fun addAll(bellScheduleItems: List<BellScheduleRequest>)
    suspend fun getAll(): List<BellScheduleResponse>
}