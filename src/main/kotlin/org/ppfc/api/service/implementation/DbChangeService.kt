package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.LookupTable
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.toLong
import org.ppfc.api.common.validateDateFormat
import org.ppfc.api.database.ChangeDtoAndGroupDto
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.model.service.change.ChangeResponse
import org.ppfc.api.model.service.classroom.ClassroomResponse
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.subject.SubjectResponse
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.InvalidDateFormatException
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.abstraction.*

class DbChangeService(private val database: Database) : ChangeService, KoinComponent {
    private val groupService: GroupService by inject()
    private val classroomService: ClassroomService by inject()
    private val teacherService: TeacherService by inject()
    private val subjectService: SubjectService by inject()

    private suspend fun getLastChangeId(): Long = withContext(Dispatchers.IO) {
        return@withContext database.changeQueries.getLastId().executeAsOne().MAX ?: -1
    }

    private suspend fun deleteGroupsWhereChange(changeId: Long) = withContext(Dispatchers.IO) {
        database.changeDtoAndGroupDtoQueries.deleteWhereChangeId(changeId = changeId)
    }

    private suspend fun getGroupsWhereChange(changeId: Long): Set<GroupResponse> = withContext(Dispatchers.IO) {
        return@withContext database.changeDtoAndGroupDtoQueries.selectWhereChangeId(
            changeId = changeId
        ).executeAsList().mapNotNull { changeDtoAndGroupDto ->
            groupService.get(id = changeDtoAndGroupDto.groupId)
        }.toSet()
    }

    private suspend fun insertGroupsWhereChange(changeId: Long, groupsIds: Set<Long>) = withContext(Dispatchers.IO) {
        groupsIds.forEach { groupId ->
            database.changeDtoAndGroupDtoQueries.insertModel(
                ChangeDtoAndGroupDto(
                    changeId = changeId,
                    groupId = groupId
                )
            )
        }
    }

    override suspend fun add(change: ChangeRequest) = withContext(Dispatchers.IO) {
        if (change.subjectId == null && change.eventName == null) {
            throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
        }
        val isSubject = change.eventName == null

        if (!validateDateFormat(pattern = "yyyy-MM-dd", date = change.date)) {
            throw InvalidDateFormatException(message = StringResource.invalidDateFormat)
        }

        database.changeQueries.insertModel(change.toDto(isSubject = isSubject))
        insertGroupsWhereChange(changeId = getLastChangeId(), groupsIds = change.groupsIds)
    }

    override suspend fun addMultiple(changes: List<ChangeRequest>) {
        changes.forEach { change ->
            add(change = change)
        }
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        date: String?,
        isNumerator: Boolean?,
        groupId: Long?,
        groupNumber: Long?,
        teacherId: Long?
    ) = withContext(Dispatchers.IO) {
        val groupsLookupTable = LookupTable<Long, GroupResponse>()
        val classroomsLookupTable = LookupTable<Long, ClassroomResponse>()
        val teachersLookupTable = LookupTable<Long, TeacherResponse>()
        val subjectsLookupTable = LookupTable<Long, SubjectResponse>()

        return@withContext try {
            database.changeQueries.selectWithParameters(
                offset = offset,
                limit = limit,
                date = date,
                isNumerator = isNumerator?.toLong(),
                teacherId = teacherId
            ).executeAsList().mapNotNull { changeDto ->
                val groups = getGroupsWhereChange(changeId = changeDto.id)
                if(groups.isEmpty()) return@mapNotNull null

                if(groupId != null && groupId !in groups.map { it.id }) return@mapNotNull null

                val classroom = changeDto.classroomId?.let { classroomId ->
                    classroomsLookupTable.getValue(classroomId) {
                        classroomService.get(classroomId)
                    }
                }

                val teacher = changeDto.teacherId?.let { teacherId ->
                    teachersLookupTable.getValue(teacherId) {
                        teacherService.get(changeDto.teacherId)
                    }
                }

                val subject = changeDto.subjectId?.let { subjectId ->
                    subjectsLookupTable.getValue(subjectId) {
                        subjectService.get(subjectId)
                    }
                }

                changeDto.toResponse(
                    groups = groups,
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

    override suspend fun get(id: Long): ChangeResponse? = withContext(Dispatchers.IO) {
        val change = database.changeQueries.selectWhereId(id = id).executeAsOneOrNull()
            ?: return@withContext null

        val groups = getGroupsWhereChange(changeId = change.id)
        if(groups.isEmpty()) return@withContext null

        val classroom = change.classroomId?.let { classroomId ->
            classroomService.get(classroomId)
        }

        val teacher = change.teacherId?.let { teacherId ->
            teacherService.get(teacherId)
        }

        val subject = change.subjectId?.let { subjectId ->
            subjectService.get(subjectId)
        }

        return@withContext change.toResponse(
            groups = groups,
            classroom = classroom,
            teacher = teacher,
            subject = subject
        )
    }

    override suspend fun update(id: Long, change: ChangeRequest) = withContext(Dispatchers.IO) {
        if (change.subjectId == null && change.eventName == null) {
            throw MalformedModelException(message = StringResource.fieldsSubjectIdAndEventNameAreNull)
        }
        val isSubject = change.eventName == null

        deleteGroupsWhereChange(changeId = id)
        insertGroupsWhereChange(changeId = id, groupsIds = change.groupsIds)

        database.changeQueries.updateWhereId(
            classroomId = change.classroomId,
            teacherId = change.teacherId,
            subjectId = change.subjectId,
            eventName = change.eventName,
            isSubject = isSubject.toLong(),
            lessonNumber = change.lessonNumber,
            date = change.date,
            dayNumber = change.dayNumber,
            isNumerator = change.isNumerator.toLong(),
            id = id
        )
    }

    override suspend fun updateMultiple(changes: Map<Long, ChangeRequest>) {
        changes.forEach { change ->
            update(id = change.key, change = change.value)
        }
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.changeQueries.deleteWhereId(id = id)
    }

    override suspend fun deleteAll() {
        database.changeQueries.deleteAll()
    }
}