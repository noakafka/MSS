package com.mss.assignment.domain.product.request

import java.math.BigDecimal

data class ProductRequest(
    val brandId: Long,
    val categoryId: Long,
    val price: BigDecimal,
)
