package org.ppfc.api.model.service.bellschedule

import kotlinx.serialization.Serializable

@Serializable
data class BellScheduleResponse(
    val id: Long,
    val lessonNumber: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)