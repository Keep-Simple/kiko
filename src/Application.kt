package com.kiko

import ReserveDto
import ReserveShortDto
import com.kiko.models.Flat
import com.kiko.services.NotificationService
import com.kiko.services.ViewSchedulerService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val notificationService = NotificationService()
    val flats = mutableMapOf(1 to Flat(1), 2 to Flat(2), 3 to Flat(2))
    val schedulerService = ViewSchedulerService(flats, notificationService)

    val server = embeddedServer(Netty, port = 8080) {
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respondText("Broken request, try again")
            }
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        routing {
            post("/reserve") {
                val (tenantId, flatId, timeCell) = call.receive<ReserveDto>()

                if (schedulerService.reserve(flatId, timeCell, tenantId))
                    call.respondText("Reserved Successfully")
                else
                    call.respondText("Reservation isn't possible")
            }
            patch("/cancel") {
                val (flatId, timeCell) = call.receive<ReserveShortDto>()
                schedulerService.cancelReservation(flatId, timeCell)
                call.respondText("Reservation cancelled")
            }
            patch("/reject") {
                val (flatId, timeCell) = call.receive<ReserveShortDto>()
                schedulerService.rejectReservation(flatId, timeCell)
                call.respondText("Reservation rejected")
            }
            patch("/approve") {
                val (flatId, timeCell) = call.receive<ReserveShortDto>()
                schedulerService.approveReservation(flatId, timeCell)
                call.respondText("Reservation approved")
            }
        }
    }
    server.start(wait = true)
}

