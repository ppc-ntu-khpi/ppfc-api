package org.ppfc.api.service.abstraction

import org.ppfc.api.model.service.discipline.DisciplineRequest
import org.ppfc.api.model.service.discipline.DisciplineResponse

interface DisciplineService {
    suspend fun add(discipline: DisciplineRequest)
    suspend fun getAll(
        offset: Long? = null,
        limit: Long? = null,
        searchQuery: String? = null
    ): List<DisciplineResponse>
    suspend fun get(id: Long): DisciplineResponse?
    suspend fun getByName(name: String): DisciplineResponse?
    suspend fun update(id: Long, discipline: DisciplineRequest)
    suspend fun delete(id: Long)
}