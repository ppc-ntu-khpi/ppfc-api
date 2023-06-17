package org.ppfc.api.model.service.bellschedule

import kotlinx.serialization.Serializable

@Serializable
data class BellScheduleRequest(
    val lessonNumber: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)