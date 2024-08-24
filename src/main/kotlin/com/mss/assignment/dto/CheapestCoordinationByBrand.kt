package com.mss.assignment.dto

import java.math.BigDecimal

data class CheapestCoordinationByBrand(
    val categoryName: String,       // 카테고리 이름
    val price: BigDecimal,          // 해당 카테고리의 최저가 상품 가격
)
