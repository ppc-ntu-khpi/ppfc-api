/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.data.config

interface ConfigProvider {
    fun getConfig(): Config
}