/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api


import io.ktor.server.application.*
import org.ppfc.api.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureDependencyInjection()
    configureCors()
    configureSerialization()
    configureSecurity()
    configureRouting()
    configureContentRouting()
    configureApiRouting()
}
