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
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

/**
 * timeSlot in api requests explanation:
 * day - number from 0-6 which represents Monday-Saturday
 * slot - number from 0-32, represents time 10:00, 10:20 ... 19:40, 20:00
 */
fun Application.module(testing: Boolean = false) {
    val notificationService = NotificationService()
    val flats = mapOf(1 to Flat(1), 2 to Flat(2), 3 to Flat(3))
    val schedulerService = ViewSchedulerService(flats, notificationService)

    routing {
        post("/reserve") {
            call.respond(schedulerService.reserve(call.receive()))
        }
        patch("/reject") {
            call.respond(schedulerService.rejectReservation(call.receive()))
        }
        patch("/approve") {
            call.respond(schedulerService.approveReservation(call.receive()))
        }
        patch("/cancel") {
            call.respond(schedulerService.cancelReservation(call.receive()))
        }
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
}

