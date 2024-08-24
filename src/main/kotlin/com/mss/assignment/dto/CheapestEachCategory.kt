package com.mss.assignment.dto

import java.math.BigDecimal

data class CheapestEachCategory(
    val items: List<ProductDto>,
    val totalPrice: BigDecimal
) {
    companion object {
        fun fromProductDtoList(products: List<ProductDto>): CheapestEachCategory {
            val totalPrice = products.sumOf { it.price }
            return CheapestEachCategory(
                items = products,
                totalPrice = totalPrice
            )
        }
    }
}
