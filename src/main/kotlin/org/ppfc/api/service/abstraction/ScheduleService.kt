/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.schedule.ScheduleResponse

interface ScheduleService {
    suspend fun add(schedule: ScheduleRequest)
    suspend fun addMultiple(schedule: List<ScheduleRequest>)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        dayNumber:Long? = null,
        isNumerator: Boolean? = null,
        groupId: Long? = null,
        groupNumber: Long? = null,
        teacherId: Long? = null
    ): List<ScheduleResponse>
    suspend fun get(id: Long): ScheduleResponse?
    suspend fun update(id: Long, schedule: ScheduleRequest)
    suspend fun updateMultiple(schedule: Map<Long, ScheduleRequest>)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}