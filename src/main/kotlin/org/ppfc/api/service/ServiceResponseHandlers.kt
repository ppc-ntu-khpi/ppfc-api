package org.ppfc.api.service

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.ppfc.api.common.StringResource

suspend inline fun <reified T> standardServiceResponseHandler(
    result: () -> T,
    call: ApplicationCall,
    noinline onSuccess: ((T) -> T)? = null
) {
    runCatching {
        val data = result()
        if (data == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = StringResource.resourceNotFound
            )
        } else {
            val responseData = onSuccess?.invoke(data) ?: data
            call.respond(
                status = HttpStatusCode.OK,
                message = responseData
            )
        }
    }.onFailure { cause ->
        cause.printStackTrace()
        call.respond(
            status = HttpStatusCode.InternalServerError,
            message = "Error: ${cause::class.simpleName}."
        )
    }
}