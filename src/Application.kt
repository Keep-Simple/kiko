package com.kiko

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
                if (schedulerService.reserve(call.receive()))
                    call.respondText("Reserved Successfully")
                else
                    call.respondText("Reservation isn't possible")
            }
            patch("/cancel") {
                schedulerService.cancelReservation(call.receive())
                call.respondText("Reservation cancelled")
            }
            patch("/reject") {
                schedulerService.rejectReservation(call.receive())
                call.respondText("Reservation rejected")
            }
            patch("/approve") {
                schedulerService.approveReservation(call.receive())
                call.respondText("Reservation approved")
            }
            get("/") {
                call.respondText("Hello from Ktor Testable sample application")
            }
        }
    }
    server.start(wait = true)
}

