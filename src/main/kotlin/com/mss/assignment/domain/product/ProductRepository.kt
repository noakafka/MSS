package com.mss.assignment.domain.product

import com.mss.assignment.dto.CheapestCoordinationByBrand
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = ["brand", "category"])
    @Query("""
        SELECT p FROM Product p
        WHERE p.price = (
            SELECT MIN(p2.price)
            FROM Product p2
            WHERE p2.category.id = p.category.id
        )
        AND p.updatedAt = (
            SELECT MAX(p3.updatedAt)
            FROM Product p3
            WHERE p3.category.id = p.category.id
            AND p3.price = p.price
        )
        ORDER BY p.category.id
    """)
    fun findCheapestByCategoryOrderByCategory(): List<Product>

    @EntityGraph(attributePaths = ["brand", "category"])
    override fun findById(id: Long): Optional<Product>

    @EntityGraph(attributePaths = ["brand", "category"])
    fun findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc(categoryName: String): Optional<Product>

    @EntityGraph(attributePaths = ["brand", "category"])
    fun findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc(categoryName: String): Optional<Product>

    @EntityGraph(attributePaths = ["brand", "category"])
//    @Query("""
//        SELECT b.name, c.name, MIN(p.price) AS price
//        FROM Product p
//        JOIN p.category c
//        JOIN p.brand b
//        WHERE p.brand.id = (
//            SELECT subquery.brand.id
//            FROM Product subquery
//            GROUP BY subquery.brand.id
//            HAVING SUM(subquery.price) = (
//                SELECT MIN(innerQuery.totalPrice)
//                FROM (
//                    SELECT SUM(p2.price) AS totalPrice
//                    FROM Product p2
//                    GROUP BY p2.brand.id
//                ) AS innerQuery
//            )
//        )
//        GROUP BY b.name, c.name
//        ORDER BY c.id ASC
//    """
//    )
    @Query("""
        SELECT p 
        FROM Product p
        WHERE p.brand.id = :brandId
        AND p.id = (
            SELECT MAX(p2.id)
            FROM Product p2
            WHERE p2.brand.id = p.brand.id
            AND p2.category.id = p.category.id
            AND p2.price = p.price
        )
        AND p.price = (
            SELECT MIN(p2.price)
            FROM Product p2
            WHERE p2.brand.id = :brandId
            AND p2.category.id = p.category.id
        )
    """)
    fun findCheapestCoordinationByBrands(@Param("brandId") brandId: Long): List<Product>
}

data class CategoryBrandPrice(
    val categoryName: String,
    val brandName: String,
    val price: BigDecimal
)

interface CheapestBrand {
    val id: Long
    val name: String
    val totalPrice: BigDecimal
}