package com.ibood.appreciation

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

fun Application.installRouting(kodein: Kodein) {
    val productRepository by kodein.instance<ProductRepository>()

    routing {
        get("/") {
            call.respond(RootNavigationDto(BASE_URL))
        }
        get("/products") {
            call.respond(productRepository.all().map { it.toProductListDto(BASE_URL) })
        }
    }
}

private fun Product.toProductListDto(baseUrl: String) = ProductListDto(
    id = id,
    title = title,
    detailUrl = "$baseUrl/products/$id"
)

@Suppress("unused")
class RootNavigationDto(baseUrl: String) {
    val products = "$baseUrl/products"
}

data class ProductListDto(
    val id: String,
    val title: String,
    val detailUrl: String
)

data class ProductDetailDto(
    val id: String,
    val title: String,
    val priceInCents: Int
)
