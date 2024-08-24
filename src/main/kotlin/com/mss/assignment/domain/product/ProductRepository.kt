package com.mss.assignment.domain.product

import com.mss.assignment.dto.CheapestCoordinationByBrand
import com.mss.assignment.dto.MinMaxPrice
import com.mss.assignment.dto.PriceSummary
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

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

    @EntityGraph(attributePaths = ["brand", "category"])
    override fun findById(id: Long): Optional<Product>

    @Query("""
        SELECT new com.mss.assignment.dto.MinMaxPrice(MIN(p.price), MAX(p.price))
        FROM Product p
        JOIN p.category c
        WHERE c.name = :categoryName
    """)
    fun findMinMaxPriceByCategoryName(@Param("categoryName") categoryName: String): MinMaxPrice

    @EntityGraph(attributePaths = ["brand", "category"])
    fun findProductsByCategoryNameAndPrice(categoryName: String, price: BigDecimal): List<PriceSummary.ProductWithBrandAndPrice>

    @Query(value = """
    WITH 
    BrandCategoryMinPrices AS (
        SELECT p.brand_id, MIN(p.price) AS min_price
        FROM product p
        JOIN category c ON p.category_id = c.id
        GROUP BY p.brand_id, c.id
    ),
    BrandTotalPrices AS (
        SELECT b.id AS id, b.name AS name, SUM(bcmp.min_price) AS total_price
        FROM BrandCategoryMinPrices bcmp
        JOIN brand b ON bcmp.brand_id = b.id
        GROUP BY b.id, b.name
    )
    
    SELECT id, name, total_price AS totalPrice
    FROM BrandTotalPrices
    ORDER BY total_price ASC
    LIMIT 1;
    """, nativeQuery = true)
    fun findCheapestBrand(): CheapestBrand

    @Query("""
        SELECT new com.mss.assignment.dto.CheapestCoordinationByBrand(
          c.name, MIN(p.price)
        )
        FROM Product p
        JOIN category c
        JOIN brand b
        WHERE b.id = :brandId
        GROUP BY b.id, c.id
    """)
    fun findCheapestCoordinationByBrands(@Param("brandId") brandId: Long): List<CheapestCoordinationByBrand>
}

 interface CheapestBrand {
     val id: Long
     val name: String
     val totalPrice: BigDecimal
 }