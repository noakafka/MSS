package com.mss.assignment.domain.product

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.category.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.Test

@DataJpaTest
class ProductRepositoryTest(
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val brandRepository: BrandRepository
) {

    @Test
    fun `findLowestPriceProductsByCategoryIds - 한 카테고리 내에 최저가 상품이 여러 개 존재하는 경우 updatedAt이 가장 최신인 하나의 상품만 반환`() {
        // given
        val category = categoryRepository.save(Category(name = "Electronics"))
        val brand = brandRepository.save(Brand(name = "BrandA"))

        // 동일한 카테고리에 동일한 가격을 가진 두 개의 상품 저장
        val product1 = productRepository.save(Product(brand = brand, category = category, price = BigDecimal(1000)))
        val product2 = productRepository.save(Product(brand = brand, category = category, price = BigDecimal(1000)))

        // when
        val result = productRepository.findLowestPriceProductsByCategoryIds(listOf(category.id))

        // then
        assertEquals(1, result.size)
        assertThat(result.first()).isEqualTo(product2.id) // 둘 중 하나만 포함
    }

    @Test
    fun `findLowestPriceProductsByCategoryIds - 카테고리별로 최저가 상품을 정확하게 반환`() {
        // given
        val category1 = categoryRepository.save(Category(name = "상의"))
        val category2 = categoryRepository.save(Category(name = "하의"))
        val brand1 = brandRepository.save(Brand(name = "BrandA"))
        val brand2 = brandRepository.save(Brand(name = "BrandB"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(500)))
        val product2 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(300)))
        val product3 = productRepository.save(Product(brand = brand2, category = category2, price = BigDecimal(700)))
        val product4 = productRepository.save(Product(brand = brand2, category = category2, price = BigDecimal(400)))

        // when
        val result = productRepository.findLowestPriceProductsByCategoryIds(listOf(category1.id!!, category2.id!!))

        // then
        assertEquals(2, result.size)
        assertTrue(result.contains(product2.id)) // category1의 최저가
        assertTrue(result.contains(product4.id)) // category2의 최저가
    }

    @Test
    fun `findProductsWithBrandAndCategory - 상품 목록과 연관된 브랜드와 카테고리를 모두 조회`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Electronics"))
        val category2 = categoryRepository.save(Category(name = "Books"))
        val brand1 = brandRepository.save(Brand(name = "BrandA"))
        val brand2 = brandRepository.save(Brand(name = "BrandB"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(500)))
        val product2 = productRepository.save(Product(brand = brand2, category = category2, price = BigDecimal(300)))

        // when
        val result = productRepository.findProductsWithBrandAndCategory(listOf(product1.id!!, product2.id!!))

        // then
        assertEquals(2, result.size)
        val productResult1 = result.first { it.id == product1.id }
        val productResult2 = result.first { it.id == product2.id }

        assertEquals(category1.id, productResult1.category.id)
        assertEquals(brand1.id, productResult1.brand.id)
        assertEquals(category2.id, productResult2.category.id)
        assertEquals(brand2.id, productResult2.brand.id)
    }

    @Test
    fun `findLowestPriceProductsByCategoryIds - 카테고리에 해당하는 상품이 없는 경우 빈 리스트 반환`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Electronics"))

        // when
        val result = productRepository.findLowestPriceProductsByCategoryIds(listOf(category1.id!!))

        // then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findProductsWithBrandAndCategory - 비어있는 상품 ID 리스트를 전달한 경우 빈 리스트 반환`() {
        // when
        val result = productRepository.findProductsWithBrandAndCategory(emptyList())

        // then
        assertTrue(result.isEmpty())
    }
}