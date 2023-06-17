package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.GenerateChangesDocumentResponse
import org.ppfc.api.model.service.change.ChangeRequest
import org.ppfc.api.service.ChangesWordDocumentGenerator
import org.ppfc.api.service.abstraction.ChangeService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.changeRouting() {
    val changeService: ChangeService by inject()
    val changesWordDocumentGenerator: ChangesWordDocumentGenerator by inject()

    route("/change") {
        get {
            standardServiceResponseHandler(
                result = {
                    changeService.getAll(
                        offset = call.request.queryParameters["offset"]?.toLongOrNull(),
                        limit = call.request.queryParameters["limit"]?.toLongOrNull(),
                        date = call.request.queryParameters["date"],
                        isNumerator = call.request.queryParameters["isNumerator"]?.toBooleanStrictOrNull(),
                        groupId = call.request.queryParameters["groupId"]?.toLongOrNull(),
                        groupNumber = call.request.queryParameters["groupNumber"]?.toLongOrNull(),
                        teacherId = call.request.queryParameters["teacherId"]?.toLongOrNull()
                    )
                },
                call = call
            )
        }

        get("/generateWordDocument") {
            standardServiceResponseHandler(
                result = {
                    val date = call.request.queryParameters["date"] ?: run {
                        call.respond(status = HttpStatusCode.BadRequest, message = StringResource.dateParameterNotFound)
                        return@get
                    }

                    val changes = changeService.getAll(date = date)
                    val fileBytes = changesWordDocumentGenerator.generate(changes = changes)

                    if(fileBytes == null) {
                        GenerateChangesDocumentResponse(error = "NO_CHANGES")
                    } else {
                        GenerateChangesDocumentResponse(
                            fileName = "$date.docx",
                            fileBytes = fileBytes
                        )
                    }
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
                    changeService.get(id)
                },
                call = call
            )
        }

        post {
            val change = call.receive<ChangeRequest>()

            standardServiceResponseHandler(
                result = {
                    changeService.add(change = change)
                },
                call = call
            )
        }

        put("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: run {
                call.respond(status = HttpStatusCode.BadRequest, message = StringResource.idPathParameterNotFound)
                return@put
            }

            val change = call.receive<ChangeRequest>()

            standardServiceResponseHandler(
                result = {
                    changeService.update(id = id, change = change)
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
                        changeService.delete(id = id)
                    }
                },
                call = call
            )
        }

        delete("/all") {
            standardServiceResponseHandler(
                result = {
                    changeService.deleteAll()
                },
                call = call
            )
        }
    }
}