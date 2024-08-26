package com.mss.assignment.dto

data class CheapestAndMostExpensiveByCategory(
    val categoryName: String,
    val cheapestProduct: ProductWithBrandAndPrice,
    val mostExpensiveProduct: ProductWithBrandAndPrice,
) {
    data class ProductWithBrandAndPrice(
        val brandName: String,
        val price: Int,
    )
}
