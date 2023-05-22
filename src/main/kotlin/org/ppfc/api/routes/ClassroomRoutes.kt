package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.classroom.ClassroomRequest
import org.ppfc.api.service.abstraction.ClassroomService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.classroomRouting() {
    val classroomService: ClassroomService by inject()

    route("/classroom") {
        get {
            standardServiceResponseHandler(
                result = {
                    classroomService.getAll(
                        offset = call.request.queryParameters["offset"]?.toLongOrNull(),
                        limit = call.request.queryParameters["limit"]?.toLongOrNull(),
                        searchQuery = call.request.queryParameters["query"]
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
                    classroomService.get(id)
                },
                call = call
            )
        }

        post {
            val classroom = call.receive<ClassroomRequest>()

            standardServiceResponseHandler(
                result = {
                    classroomService.add(classroom = classroom)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val classroom = call.receive<ClassroomRequest>()

            standardServiceResponseHandler(
                result = {
                    classroomService.update(id = id, classroom = classroom)
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
                        classroomService.delete(id = id)
                    }
                },
                call = call
            )
        }
    }
}