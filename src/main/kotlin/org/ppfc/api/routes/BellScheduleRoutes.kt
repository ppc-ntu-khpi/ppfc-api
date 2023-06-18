package org.ppfc.api.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
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
            val bellScheduleItems = call.receive<List<BellScheduleRequest>>()

            standardServiceResponseHandler(
                result = {
                    bellScheduleService.add(bellScheduleItems = bellScheduleItems)
                },
                call = call
            )
        }
    }
}