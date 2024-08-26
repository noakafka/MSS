package com.mss.assignment.dto

import com.mss.assignment.domain.product.Product

data class ProductDto(
    val id: Long,
    val categoryName: String,
    val brandName: String,
    val price: Int,
) {
    companion object {
        fun fromEntity(product: Product): ProductDto {
            return ProductDto(
                id = product.id,
                categoryName = product.category.name,
                brandName = product.brand.name,
                price = product.price.toInt(),
            )
        }
    }
}
