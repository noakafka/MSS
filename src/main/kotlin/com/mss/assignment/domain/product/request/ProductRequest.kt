package com.mss.assignment.domain.product.request

import java.math.BigDecimal

data class ProductRequest(
    val brandName: String,
    val categoryName: String,
    val price: BigDecimal,
)
