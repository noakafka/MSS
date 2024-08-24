package com.mss.assignment.dto

import java.math.BigDecimal

data class CheapestAndMostExpensiveByCategory(
    val categoryName: String,
    val cheapestProduct: ProductWithBrandAndPrice,
    val mostExpensiveProduct: ProductWithBrandAndPrice,
) {
    data class ProductWithBrandAndPrice(
        val brandName: String,
        val price: BigDecimal
    )
}