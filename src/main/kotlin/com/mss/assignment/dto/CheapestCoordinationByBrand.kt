package com.mss.assignment.dto

import java.math.BigDecimal

data class CheapestCoordinationByBrand(
    // 카테고리 이름
    val categoryName: String,
    // 해당 카테고리의 최저가 상품 가격
    val price: BigDecimal,
)
