package com.mss.assignment.dto

import com.mss.assignment.domain.brand.Brand

data class BrandDto(
    val id: Long,
    val name: String,
) {
    companion object {
        fun fromEntity(brand: Brand): BrandDto {
            return BrandDto(
                id = brand.id,
                name = brand.name,
            )
        }
    }
}
