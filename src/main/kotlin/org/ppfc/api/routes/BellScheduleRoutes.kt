package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.bellschedule.BellScheduleRequest
import org.ppfc.api.service.abstraction.BellScheduleService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.bellScheduleRouting() {
    val bellScheduleService: BellScheduleService by inject()

    route("/bellSchedule") {
        get {
            standardServiceResponseHandler(
                result = {
                    bellScheduleService.getAll()
                },
                call = call
            )
        }

        post {
            val bellScheduleItem = call.receive<BellScheduleRequest>()

            standardServiceResponseHandler(
                result = {
                    bellScheduleService.add(bellScheduleItem = bellScheduleItem)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val bellScheduleItem = call.receive<BellScheduleRequest>()

            standardServiceResponseHandler(
                result = {
                    bellScheduleService.update(id = id, bellScheduleItem = bellScheduleItem)
                },
                call = call
            )
        }

        delete("{ids?}") {
            val idsText = call.parameters["ids"] ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idsPathParameterNotFound)
                return@delete
            }

            val ids = idsText.toIdsList() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.argumentFormatError)
                return@delete
            }

            standardServiceResponseHandler(
                result = {
                    ids.forEach { id ->
                        bellScheduleService.delete(id = id)
                    }
                },
                call = call
            )
        }
    }
}