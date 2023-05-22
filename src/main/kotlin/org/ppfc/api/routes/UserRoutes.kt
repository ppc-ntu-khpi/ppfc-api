package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.common.StringResource
import org.ppfc.api.model.service.user.UserRequest
import org.ppfc.api.service.abstraction.UserService
import org.ppfc.api.service.standardServiceResponseHandler

fun Route.userRouting() {
    val userService: UserService by inject()

    route("/user") {
        get {
            standardServiceResponseHandler(
                result = {
                    userService.getAll(
                        offset = call.request.queryParameters["offset"]?.toLongOrNull(),
                        limit = call.request.queryParameters["limit"]?.toLongOrNull(),
                        searchQuery = call.request.queryParameters["query"],
                        isStudent = call.request.queryParameters["isStudent"]?.toBooleanStrictOrNull()
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
                    userService.get(id = id)
                },
                call = call
            )
        }

        post {
            val user = call.receive<UserRequest>()

            standardServiceResponseHandler(
                result = {
                    userService.add(user = user)
                },
                call = call
            )
        }

        put {
            val user = call.receive<UserRequest>()

            standardServiceResponseHandler(
                result = {
                    userService.update(user = user)
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
                        userService.delete(id = id)
                    }
                },
                call = call
            )
        }
    }
}