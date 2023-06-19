/*
 * Copyright (c) 2023. Vitalii Kozyr
 */

package org.ppfc.api.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.IOException

fun Application.configureContentRouting() {
    val contentPath = "webcontent"

    routing {
        staticResources("/static", "/static")

        get("content") {
            try {
                val fileLinks = mutableListOf<String>()

                val root = File(contentPath)
                root.walkTopDown().forEach { file ->
                    if (file.isFile) {
                        val relativePath = root.toPath().relativize(file.toPath())
                        fileLinks.add("<a href='/${relativePath}'>${relativePath}</a>")
                    }
                }

                val fileLinksString = fileLinks.joinToString("<br>")
                call.respondText(fileLinksString, ContentType.Text.Html)
            } catch (_: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get("/{file...}") {
            val file = call.parameters.getAll("file")?.joinToString(separator = "/")
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            try {
                call.respondFile(File("$contentPath/$file"))
            } catch (e: IOException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (_: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        authenticate {
            post("/uploadFiles/{savePath...}") {
                val clearOldFiles = call.request.queryParameters["clearOldFiles"]?.toBoolean()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                val savePath = call.parameters.getAll("savePath")?.joinToString(separator = "/")
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                val fullPath = "$contentPath/$savePath"

                try {
                    if (clearOldFiles) {
                        File(fullPath).deleteRecursively()
                    }
                    File(fullPath).mkdirs()

                    call.receiveMultipart().forEachPart { part ->
                        if (part !is PartData.FileItem) return@forEachPart

                        val fileName = part.originalFileName ?: return@forEachPart
                        val fileBytes = part.streamProvider().readBytes()
                        File("$fullPath/$fileName").writeBytes(fileBytes)

                        part.dispose()
                    }

                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}