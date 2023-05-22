package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.subject.SubjectRequest
import org.ppfc.api.service.abstraction.SubjectService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.subjectRouting() {
    val subjectService: SubjectService by inject()

    route("/subject") {
        get {
            standardServiceResponseHandler(
                result = {
                    subjectService.getAll(
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
                    subjectService.get(id)
                },
                call = call
            )
        }

        post {
            val subject = call.receive<SubjectRequest>()

            standardServiceResponseHandler(
                result = {
                    subjectService.add(subject = subject)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val subject = call.receive<SubjectRequest>()

            standardServiceResponseHandler(
                result = {
                    subjectService.update(id = id, subject = subject)
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
                        subjectService.delete(id = id)
                    }
                },
                call = call
            )
        }
    }
}