package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.LookupTable
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.abstraction.*

class DbScheduleService(private val database: Database) : ScheduleService, KoinComponent {
    private val groupService: GroupService by inject()
    private val classroomService: ClassroomService by inject()
    private val teacherService: TeacherService by inject()
    private val subjectService: SubjectService by inject()

    override suspend fun add(schedule: ScheduleRequest) = withContext(Dispatchers.IO) {
        if (schedule.subjectId == null && schedule.eventName == null) {
            throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
        }
        val isSubject = schedule.eventName == null

        database.scheduleQueries.insertModel(schedule.toDto(isSubject = isSubject))
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        dayNumber: Long?,
        isNumerator: Boolean?,
        groupId: Long?,
        groupNumber: Long?,
        teacherId: Long?
    ) = withContext(Dispatchers.IO) {
        val groupsLookupTable = LookupTable<Long, GroupResponse>()
        val classroomsLookupTable = LookupTable<Long, ClassroomResponse>()
        val teachersLookupTable = LookupTable<Long, TeacherResponse>()
        val subjectsLookupTable = LookupTable<Long, SubjectResponse>()

        val groupIdParam = groupId ?: groupNumber?.let {
            groupService.getByNumber(groupNumber)?.id ?: 0L
        }

        return@withContext try {
            database.scheduleQueries.selectWithParameters(
                offset = offset,
                limit = limit,
                dayNumber = dayNumber,
                isNumerator = isNumerator?.toLong(),
                groupId = groupIdParam,
                teacherId = teacherId
            ).executeAsList().mapNotNull { scheduleDto ->
                val group = groupsLookupTable.getValue(scheduleDto.groupId) {
                    groupService.get(scheduleDto.groupId)
                } ?: return@mapNotNull null

                val classroom = classroomsLookupTable.getValue(scheduleDto.classroomId) {
                    classroomService.get(scheduleDto.classroomId)
                } ?: return@mapNotNull null

                val teacher = teachersLookupTable.getValue(scheduleDto.teacherId) {
                    teacherService.get(scheduleDto.teacherId)
                } ?: return@mapNotNull null

                val subject = scheduleDto.subjectId?.let { subjectId ->
                    subjectsLookupTable.getValue(subjectId) {
                        subjectService.get(subjectId)
                    }
                }

                scheduleDto.toResponse(
                    group = group,
                    classroom = classroom,
                    teacher = teacher,
                    subject = subject
                )
            }
        } finally {
            groupsLookupTable.clear()
            classroomsLookupTable.clear()
            teachersLookupTable.clear()
            subjectsLookupTable.clear()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        val schedule = database.scheduleQueries.selectWhereId(id = id).executeAsOneOrNull()
            ?: return@withContext null

        val group = groupService.get(schedule.groupId) ?: return@withContext null
        val classroom = classroomService.get(schedule.classroomId) ?: return@withContext null
        val teacher = teacherService.get(schedule.teacherId) ?: return@withContext null

        val subject = schedule.subjectId?.let { subjectId ->
            subjectService.get(subjectId)
        }

        return@withContext schedule.toResponse(
            group = group,
            classroom = classroom,
            teacher = teacher,
            subject = subject
        )
    }

    override suspend fun update(id: Long, schedule: ScheduleRequest) = withContext(Dispatchers.IO) {
        if (schedule.subjectId == null && schedule.eventName == null) {
            throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
        }
        val isSubject = schedule.eventName == null

        database.scheduleQueries.updateWhereId(
            groupId = schedule.groupId,
            classroomId = schedule.classroomId,
            teacherId = schedule.teacherId,
            subjectId = schedule.subjectId,
            eventName = schedule.eventName,
            isSubject = isSubject.toLong(),
            lessonNumber = schedule.lessonNumber,
            dayNumber = schedule.dayNumber,
            isNumerator = schedule.isNumerator.toLong(),
            id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.scheduleQueries.deleteWhereId(id = id)
    }

    override suspend fun deleteAll() {
        database.scheduleQueries.deleteAll()
    }
}