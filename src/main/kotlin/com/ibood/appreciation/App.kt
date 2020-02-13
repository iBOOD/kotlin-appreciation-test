package com.ibood.appreciation

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging.logger
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

object App {
    private val log = logger {}
    @JvmStatic
    fun main(args: Array<String>) {
        log.info { "Starting up application." }
        embeddedServer(factory = Netty, port = 8080) {
            mainKodeined(applicationKodein())
        }.start(wait = true)
    }
}

fun applicationKodein() = Kodein {
    bind<ProductRepository>()  with singleton { InMemoryProductRepository() }
}

fun Application.mainKodeined(kodein: Kodein) {
    val productRepository by kodein.instance<ProductRepository>()

    routing {
        get("/products") {
            call.respond(productRepository.all())
        }
    }

    install(ContentNegotiation) {
        jackson {  }
    }
}
