package com.mss.assignment.domain.brand

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BrandRepository : JpaRepository<Brand, Long> {
    @Query(
        """
        SELECT b FROM Brand b
        WHERE b.id = (
            SELECT subquery.brand_id FROM (
              SELECT p.brand.id AS brand_id, p.category.id AS category_id, MIN(p.price) AS price
              FROM Product p
              GROUP BY p.brand.id, p.category.id
            ) as subquery
            GROUP BY subquery.brand_id
            ORDER BY SUM(subquery.price) ASC, subquery.brand_id DESC
            LIMIT 1
        )
    """,
    )
    fun findCheapestBrand(): Optional<Brand>

    fun findByName(name: String): Optional<Brand>

    fun existsByName(name: String): Boolean
}
