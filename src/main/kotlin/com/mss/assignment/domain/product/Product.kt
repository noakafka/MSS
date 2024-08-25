package com.mss.assignment.domain.product

import com.mss.assignment.domain.BaseEntity
import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.category.Category
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
class Product(
    brand: Brand,
    category: Category,
    price: BigDecimal,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToOne
    @JoinColumn(name = "brand_id")
    var brand: Brand = brand
        private set

    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: Category = category
        private set

    var price: BigDecimal = price
        private set

    fun update(
        brand: Brand,
        category: Category,
        price: BigDecimal,
    ) {
        this.brand = brand
        this.category = category
        this.price = price
    }
}
