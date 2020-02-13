package com.ibood.appreciation

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging.logger
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

const val PORT = 8080

object App {
    private val log = logger {}
    @JvmStatic
    fun main(args: Array<String>) {
        log.info { "Starting up application." }
        embeddedServer(factory = Netty, port = PORT) {
            mainKodeined(applicationKodein())
        }.start(wait = true)
    }
}

fun applicationKodein() = Kodein {
    bind<ProductRepository>() with singleton { InMemoryProductRepository() }
}

fun Application.mainKodeined(kodein: Kodein) {
    install(ContentNegotiation) {
        jackson { }
    }
    installRouting(kodein)
}
