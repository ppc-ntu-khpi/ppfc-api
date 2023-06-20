/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.service.abstraction.AccessKeyService
import org.ppfc.api.service.standardServiceResponseHandler
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Route.accessKeyRouting() {
    val accessKeyService: AccessKeyService by inject()

    route("/accessKey") {
        get("/generate") {
            standardServiceResponseHandler(
                result = {
                    accessKeyService.generateAccessKey(expiration = 3.toDuration(unit = DurationUnit.MINUTES))
                },
                call = call
            )
        }

        get("/verify/{key?}") {
            val key = call.parameters["key"] ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.keyParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = {
                    accessKeyService.verifyAccessKey(key = key)
                },
                call = call
            )
        }
    }
}