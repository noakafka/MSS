package com.mss.assignment.domain.coordination.response

import com.mss.assignment.dto.CheapestCoordinationByBrand
import java.math.BigDecimal

data class CheapestCoordinationByBrandResponse(
    val brandName: String,
    val cheapestProductsByCategory: List<CheapestCoordinationByBrand>,
    val totalPrice: BigDecimal
)
