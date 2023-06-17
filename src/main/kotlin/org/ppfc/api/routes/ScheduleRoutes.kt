package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.schedule.ScheduleRequest
import org.ppfc.api.service.abstraction.ScheduleService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.scheduleRouting() {
    val scheduleService: ScheduleService by inject()

    route("/schedule") {
        get {
            standardServiceResponseHandler(
                result = {
                    scheduleService.getAll(
                        offset = call.request.queryParameters["offset"]?.toLongOrNull(),
                        limit = call.request.queryParameters["limit"]?.toLongOrNull(),
                        dayNumber = call.request.queryParameters["dayNumber"]?.toLongOrNull(),
                        isNumerator = call.request.queryParameters["isNumerator"]?.toBooleanStrictOrNull(),
                        groupId = call.request.queryParameters["groupId"]?.toLongOrNull(),
                        groupNumber = call.request.queryParameters["groupNumber"]?.toLongOrNull(),
                        teacherId = call.request.queryParameters["teacherId"]?.toLongOrNull()
                    )
                },
                call = call
            )
        }

        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = {
                    scheduleService.get(id)
                },
                call = call
            )
        }

        post {
            val schedule = call.receive<ScheduleRequest>()

            standardServiceResponseHandler(
                result = {
                    scheduleService.add(schedule = schedule)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val schedule = call.receive<ScheduleRequest>()

            standardServiceResponseHandler(
                result = {
                    scheduleService.update(id = id, schedule = schedule)
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
                        scheduleService.delete(id = id)
                    }
                },
                call = call
            )
        }

        delete("/all") {
            standardServiceResponseHandler(
                result = {
                    scheduleService.deleteAll()
                },
                call = call
            )
        }
    }
}