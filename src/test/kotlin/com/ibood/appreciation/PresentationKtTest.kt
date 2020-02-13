package com.ibood.appreciation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.testng.annotations.Test

@Test
class PresentationKtTest {

    private val id = "testId"
    private val baseUrl = "testBaseUrl"
    private val product = Product.any()

    fun `When convert Product to List DTO Then construct proper URL`() {
        assertThat(product.copy(id = id).toProductListDto(baseUrl).detailUrl)
            .isEqualTo("$baseUrl/products/$id")
    }
}
