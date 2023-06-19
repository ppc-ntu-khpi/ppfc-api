/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.schedule

import kotlinx.serialization.Serializable
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.teacher.TeacherResponse

@Serializable
data class ScheduleResponse(
    val id: Long,
    val group: GroupResponse,
    val classroom: ClassroomResponse,
    val teacher: TeacherResponse,
    val subject: SubjectResponse?,
    val eventName: String?,
    val isSubject: Boolean,
    val lessonNumber: Long,
    val dayNumber: Long,
    val isNumerator: Boolean
)