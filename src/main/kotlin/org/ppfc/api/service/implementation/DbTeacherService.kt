package org.ppfc.api.service.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.common.LookupTable
import org.ppfc.api.common.toLong
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.discipline.DisciplineResponse
import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.model.service.teacher.TeacherResponse
import org.ppfc.api.model.service.toDto
import org.ppfc.api.model.service.toResponse
import org.ppfc.api.service.abstraction.DisciplineService
import org.ppfc.api.service.abstraction.TeacherService

class DbTeacherService(private val database: Database) : TeacherService, KoinComponent {
    private val disciplineService: DisciplineService by inject()

    override suspend fun add(teacher: TeacherRequest) = withContext(Dispatchers.IO) {
        database.teacherQueries.insertModel(teacher.toDto())
    }

    override suspend fun getAll(
        offset: Long?,
        limit: Long?,
        searchQuery: String?,
        disciplineId: Long?,
        disciplineName: String?
    ) = withContext(Dispatchers.IO) {
        val disciplineLookupTable = LookupTable<Long, DisciplineResponse>()

        val disciplineIdParam = disciplineId ?: disciplineName?.let {
            disciplineService.getByName(disciplineName)?.id ?: 0L
        }

        return@withContext try {
            database.teacherQueries.selectWithParameters(
                offset = offset,
                limit = limit,
                searchQuery = searchQuery,
                disciplineId = disciplineIdParam
            ).executeAsList().mapNotNull { teacherDto ->
                val discipline = disciplineLookupTable.getValue(teacherDto.disciplineId) {
                    disciplineService.get(teacherDto.disciplineId)
                } ?: return@mapNotNull null

                teacherDto.toResponse(discipline = discipline)
            }
        } finally {
            disciplineLookupTable.clear()
        }
    }

    override suspend fun get(id: Long) = withContext(Dispatchers.IO) {
        val teacher = database.teacherQueries.selectWhereId(id = id).executeAsOneOrNull()
            ?: return@withContext null

        val discipline = disciplineService.get(teacher.disciplineId)
            ?: return@withContext null

        teacher.toResponse(discipline = discipline)
    }

    override suspend fun getByFirstAndLastName(
        firstName: String,
        lastName: String
    ): TeacherResponse? = withContext(Dispatchers.IO) {
        val teacher = database.teacherQueries.selectWhereFirstAndLastName(
            firstName = firstName,
            lastName = lastName
        ).executeAsOneOrNull()
            ?: return@withContext null

        val discipline = disciplineService.get(teacher.disciplineId)
            ?: return@withContext null

        teacher.toResponse(discipline = discipline)
    }

    override suspend fun update(id: Long, teacher: TeacherRequest) = withContext(Dispatchers.IO) {
        database.teacherQueries.updateWhereId(
            firstName = teacher.firstName,
            middleName = teacher.middleName,
            lastName = teacher.lastName,
            disciplineId = teacher.disciplineId,
            isHeadTeacher = teacher.isHeadTeacher.toLong(),
            id = id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.teacherQueries.deleteWhereId(id = id)
    }
}