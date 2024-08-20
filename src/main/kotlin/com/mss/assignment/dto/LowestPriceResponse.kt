package com.mss.assignment.dto

import java.math.BigDecimal

data class LowestPriceResponse(
    val items: List<ProductDto>,
    val totalPrice: BigDecimal
)
