/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.classroom

import kotlinx.serialization.Serializable

@Serializable
data class ClassroomRequest(
    val name: String
)