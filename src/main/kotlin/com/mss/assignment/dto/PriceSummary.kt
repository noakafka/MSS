package com.mss.assignment.dto

import java.math.BigDecimal

data class PriceSummary(
    val category: String,
    val minPrice: List<ProductWithBrandAndPrice>,
    val maxPrice: List<ProductWithBrandAndPrice>,
) {
    data class ProductWithBrandAndPrice(
        val brandName: String,
        val price: BigDecimal
    )
}