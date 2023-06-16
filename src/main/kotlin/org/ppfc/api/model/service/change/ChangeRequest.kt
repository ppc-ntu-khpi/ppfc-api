/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.change

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val groupsIds: Set<Long>,
    val classroomId: Long?,
    val teacherId: Long?,
    val subjectId: Long?,
    val eventName: String?,
    val lessonNumber: Long?,
    val dayNumber: Long,
    val date: String,
    val isNumerator: Boolean
)