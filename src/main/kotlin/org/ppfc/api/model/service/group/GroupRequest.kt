/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.model.service.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupRequest(
    val number: Long,
    val courseId: Long
)