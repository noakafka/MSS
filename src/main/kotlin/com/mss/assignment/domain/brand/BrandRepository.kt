package com.mss.assignment.domain.brand

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BrandRepository : JpaRepository<Brand, Long> {
    @Query("""
        SELECT b.id 
        FROM Brand b
        JOIN Product p ON b.id = p.brand.id
        JOIN Category c ON p.category.id = c.id
        GROUP BY b.id
        HAVING COUNT(DISTINCT c.id) = (SELECT COUNT(c2.id) FROM Category c2)
    """)
    fun findAllCategoryBrands(): List<Long>
}
