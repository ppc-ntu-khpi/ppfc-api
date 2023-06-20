/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.service.implementation

import org.koin.core.component.KoinComponent
import org.ppfc.api.database.AccessKeyDto
import org.ppfc.api.database.Database
import org.ppfc.api.model.service.accesskey.AccessKeyResponse
import org.ppfc.api.service.abstraction.AccessKeyService
import kotlin.random.Random
import kotlin.time.Duration

class DbAccessKeyService(private val database: Database) : AccessKeyService, KoinComponent {

    override suspend fun generateAccessKey(expiration: Duration): AccessKeyResponse {
        val key = Random.nextInt(1000, 10000).toString()
        val expiresAt = System.currentTimeMillis() + expiration.inWholeMilliseconds

        database.accessKeyQueries.insertModel(AccessKeyDto(key = key, expiresAt = expiresAt))

        return AccessKeyResponse(key = key, expiresAt = expiresAt)
    }

    override suspend fun verifyAccessKey(key: String): Boolean {
        val accessKey = database.accessKeyQueries.selectWhereKey(key = key).executeAsOneOrNull() ?: return false
        return (accessKey.expiresAt >= System.currentTimeMillis()).also { isValid ->
            if(isValid) database.accessKeyQueries.deleteWhereKey(key = key)
        }
    }
}