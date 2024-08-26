package com.mss.assignment.dto

data class CheapestEachCategory(
    val items: List<ProductDto>,
    val totalPrice: Int,
) {
    companion object {
        fun fromProductDtoList(products: List<ProductDto>): CheapestEachCategory {
            val totalPrice = products.sumOf { it.price }
            return CheapestEachCategory(
                items = products,
                totalPrice = totalPrice.toInt(),
            )
        }
    }
}
