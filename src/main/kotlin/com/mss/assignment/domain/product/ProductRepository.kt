package com.mss.assignment.domain.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    @Query(value = """
        SELECT subquery.id FROM (
            SELECT p.*, 
                   ROW_NUMBER() OVER (PARTITION BY p.category_id ORDER BY p.price ASC, p.updated_at DESC) AS rn
            FROM product p
            WHERE p.category_id IN :categoryIds
        ) subquery
        WHERE subquery.rn = 1
    """, nativeQuery = true)
    fun findLowestPriceProductsByCategoryIds(@Param("categoryIds") categoryIds: List<Long>): List<Long>

    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand
        JOIN FETCH p.category
        WHERE p.id IN :ids
        ORDER BY p.category.id ASC
    """)
    fun findProductsWithBrandAndCategory(@Param("ids") ids: List<Long>): List<Product>
}