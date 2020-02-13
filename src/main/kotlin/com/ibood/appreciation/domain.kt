package com.ibood.appreciation

data class Product(
    val id: String,
    val title: String,
    val priceInCents: Int
)

interface ProductRepository {
    fun all(): List<Product>
}

class InMemoryProductRepository : ProductRepository {

    private val products = listOf(
        Product("id1", "TV screen", 499_00),
        Product("id2", "XBox", 149_00),
        Product("id3", "Socks", 9_90),
        Product("id4", "Screw Driver", 2_00),
        Product("id5", "Rice Cooker", 49_00),
        Product("id6", "Shoes", 14_99)
    )

    override fun all() = products

}
