package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.teacher.TeacherRequest
import org.ppfc.api.service.abstraction.TeacherService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.teacherRouting() {
    val teacherService: TeacherService by inject()

    route("/teacher") {
        get {
            standardServiceResponseHandler(
                result = {
                    teacherService.getAll(
                        offset = call.request.queryParameters["offset"]?.toLongOrNull(),
                        limit = call.request.queryParameters["limit"]?.toLongOrNull(),
                        searchQuery = call.request.queryParameters["query"],
                        disciplineId = call.request.queryParameters["disciplineId"]?.toLongOrNull(),
                        disciplineName = call.request.queryParameters["disciplineName"]
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
                    teacherService.get(id)
                },
                call = call
            )
        }

        get("byFirstAndLastName/{firstAndLastName}") {
            val firstAndLastName = call.parameters["firstAndLastName"] ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@get
            }

            standardServiceResponseHandler(
                result = {
                    teacherService.getByFirstAndLastName(
                        firstName = firstAndLastName.substringBefore(" "),
                        lastName = firstAndLastName.substringAfter(" ")
                    )
                },
                call = call
            )
        }

        post {
            val teacher = call.receive<TeacherRequest>()

            standardServiceResponseHandler(
                result = {
                    teacherService.add(teacher = teacher)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val teacher = call.receive<TeacherRequest>()

            standardServiceResponseHandler(
                result = {
                    teacherService.update(id = id, teacher = teacher)
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
                        teacherService.delete(id = id)
                    }
                },
                call = call
            )
        }
    }
}