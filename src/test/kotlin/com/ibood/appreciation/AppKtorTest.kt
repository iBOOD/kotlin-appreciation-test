package com.ibood.appreciation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.handleRequest
import org.testng.annotations.Test

@Test
class AppKtorTest {

    private val jackson = jacksonObjectMapper()

    fun `When get products Then return 200 OK and some products`() = withKtor {
        with(handleRequest(Get, "/products")) {
            assertThat(response.status()).isEqualTo(OK)
            val json = jackson.readTree(response.content)
            assertThat(json.size()).isEqualTo(6)
            assertThat(json[0]["id"].textValue()).isEqualTo("id1")
            assertThat(json[0]["title"].textValue()).isEqualTo("TV screen")
        }
    }
}
