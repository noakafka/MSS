package com.mss.assignment.dto

import com.mss.assignment.domain.product.Product

data class ProductDto(
    val categoryName: String,
    val brandName: String,
    val price: Int,
) {
    companion object {
        fun fromEntity(product: Product): ProductDto {
            return ProductDto(
                categoryName = product.category.name,
                brandName = product.brand.name,
                price = product.price.toInt(),
            )
        }
    }
}
