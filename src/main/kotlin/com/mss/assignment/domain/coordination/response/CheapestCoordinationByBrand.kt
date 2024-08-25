package com.mss.assignment.domain.coordination.response

import com.mss.assignment.domain.product.Product
import java.math.BigDecimal

data class CheapestCoordinationByBrand(
    val brandName: String,
    val products: List<ProductWithCategoryAndPrice>,
    val totalPrice: BigDecimal,
) {
    data class ProductWithCategoryAndPrice(
        val categoryName: String,
        val price: BigDecimal,
    ) {
        companion object {
            fun fromProduct(product: Product): ProductWithCategoryAndPrice {
                return ProductWithCategoryAndPrice(
                    categoryName = product.category.name,
                    price = product.price,
                )
            }
        }
    }

    companion object {
        fun fromProductList(product: List<Product>): CheapestCoordinationByBrand {
            return CheapestCoordinationByBrand(
                brandName = product.first().brand.name,
                products = product.map { ProductWithCategoryAndPrice.fromProduct(it) },
                totalPrice = product.sumOf { it.price },
            )
        }
    }
}
