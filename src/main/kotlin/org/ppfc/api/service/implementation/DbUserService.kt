/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.LookupTable
import org.ppfc.api.common.StringResource
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.group.GroupResponse
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.service.MalformedModelException
import org.ppfc.api.service.abstraction.GroupService
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.abstraction.UserService

class DbUserService(private val database: Database) : UserService, KoinComponent {
    private val groupService: GroupService by inject()
    private val teacherService: TeacherService by inject()

    override suspend fun add(user: UserRequest) = withContext(Dispatchers.IO) {
        if (user.groupId == null && user.teacherId == null) {
            throw MalformedModelException(message = StringResource.fieldsGroupIdAndTeacherIdAreNull)
        }
        val isGroup = user.teacherId == null

        database.userQueries.insertModel(user.toDto(isGroup = isGroup))
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?,
        isStudent: Boolean?
    ) = withContext(Dispatchers.IO) {
        val groupLookupTable = LookupTable<Long, GroupResponse>()
        val teacherLookupTable = LookupTable<Long, TeacherResponse>()

        return@withContext try {
            database.userQueries.selectWithParameters(
                offset = offset,
                limit = limit,
                searchQuery = searchQuery,
                isGroup = isStudent?.toLong()
            ).executeAsList().map { userDto ->
                val group = userDto.groupId?.let {
                    groupLookupTable.getValue(userDto.groupId) {
                        groupService.get(userDto.groupId)
                    }
                }

                val teacher = userDto.teacherId?.let {
                    teacherLookupTable.getValue(userDto.teacherId) {
                        teacherService.get(userDto.teacherId)
                    }
                }

                userDto.toResponse(group = group, teacher = teacher)
            }
        } finally {
            groupLookupTable.clear()
            teacherLookupTable.clear()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        val userDto = database.userQueries.selectWhereUserCode(id = id).executeAsOneOrNull()
            ?: return@withContext null

        val group = userDto.groupId?.let {
            groupService.get(userDto.groupId) ?: return@withContext null
        }

        val teacher = userDto.teacherId?.let {
            teacherService.get(userDto.teacherId) ?: return@withContext null
        }

        return@withContext userDto.toResponse(group = group, teacher = teacher)
    }

    override suspend fun update(user: UserRequest) = withContext(Dispatchers.IO) {
        if (user.groupId == null && user.teacherId == null) {
            throw MalformedModelException(message = StringResource.fieldsGroupIdAndTeacherIdAreNull)
        }
        val isGroup = user.teacherId == null

        database.userQueries.updateWhereUserCode(
            groupId = user.groupId,
            teacherId = user.teacherId,
            isGroup = isGroup.toLong(),
            id = user.id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.userQueries.deleteWhereUserCode(id = id)
    }
}