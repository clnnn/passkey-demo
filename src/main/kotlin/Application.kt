package com.github.clnnn

import dev.hayden.KHealth
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val passkeyService = PasskeyService()

fun Application.module() {
    install(Compression)
    install(KHealth)
    install(ContentNegotiation) {
        json()
    }

    routing {
        staticResources("/static", "static")

        get("/") {
            call.respondRedirect { path("/registration") }
        }

        // Serve the registration page
        get("/registration") {
            call.respondHtml { registrationPage() }
        }

        // Start the registration process
        post("/register/start") {
            val req = call.receive<StartRegistrationRequest>()
            val res = passkeyService.startRegistration(req)

            when (res) {
                is StartRegistrationResponse.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("challenge" to res.challenge))
                }
                is StartRegistrationResponse.Error -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to res.message))
                }
            }
        }

        // Finish the registration process
        post("/register/finish") {
            val req = call.receive<FinishRegistrationRequest>()
            val res = passkeyService.finishRegistration(req)

            when (res) {
                is FinishRegistrationResponse.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to res.message))
                }
                is FinishRegistrationResponse.Error -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to res.message))
                }
            }
        }

        // Serve the authentication page
        get("/authentication") {
            call.respondHtml { loginPage() }
        }

        // Start the authentication process
        post("auth/start") {
            val req = call.receive<StartAuthenticationRequest>()
            val res = passkeyService.startAuthentication(req)

            when (res) {
                is StartAuthenticationResponse.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("challenge" to res.challenge, "credentialId" to res.credentialId))
                }
                is StartAuthenticationResponse.Error -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to res.message))
                }
            }
        }

        // Finish the authentication process
        post("auth/finish") {
            val req = call.receive<FinishAuthenticationRequest>()
            val res = passkeyService.finishAuthentication(req)

            when (res) {
                is FinishAuthenticationResponse.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to res.message))
                }
                is FinishAuthenticationResponse.Error -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to res.message))
                }
            }
        }
    }
}
