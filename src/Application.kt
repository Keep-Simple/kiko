package com.kiko

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>){
     val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
        }
    }
    server.start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient() {
        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

}

