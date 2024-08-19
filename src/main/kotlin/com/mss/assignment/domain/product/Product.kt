package com.mss.assignment.domain.product

import com.mss.assignment.domain.BaseEntity
import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.category.Category
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
data class Product(
    @ManyToOne
    @JoinColumn(name = "brand_id")
    val brand: Brand,

    @ManyToOne
    @JoinColumn(name = "category_id")
    val category: Category,

    val price: BigDecimal
) : BaseEntity()