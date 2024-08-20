package com.mss.assignment.dto

import java.math.BigDecimal

data class ProductDto(
    val categoryName: String,
    val brandName: String,
    val price: BigDecimal
)