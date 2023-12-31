/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.subject

import kotlinx.serialization.Serializable

@Serializable
data class SubjectResponse(
    val id: Long,
    val name: String
)