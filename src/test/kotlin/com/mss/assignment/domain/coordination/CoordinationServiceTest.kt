package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.category.CategoryService
import com.mss.assignment.domain.product.Product
import com.mss.assignment.domain.product.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class CoordinationServiceTest {

    @Mock
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var coordinationService: CoordinationService

    private val category1 = getCategory1()
    private val category2 = getCategory2()
    private val brand1 = getBrand1()
    private val brand2 = getBrand2()

    @Test
    fun `카테고리가 없는 경우`() {
        // given
        `when`(categoryService.findAllCategoryIds()).thenReturn(emptyList())

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertTrue(response.items.isEmpty())
        assertEquals(BigDecimal.ZERO, response.totalPrice)
    }

    @Test
    fun `모든 카테고리에 대해 최저가 상품이 존재하는 경우`() {
        // given
        val categoryIds = listOf(category1.id!!, category2.id!!)
        val product1 = MockProductBuilder(id = 1L, brand = brand1, category = category1, price = BigDecimal(1000)).build()
        val product2 = MockProductBuilder(id = 2L, brand = brand2, category = category2, price = BigDecimal(2000)).build()
        `when`(categoryService.findAllCategoryIds()).thenReturn(categoryIds)
        `when`(productRepository.findLowestPriceProductsByCategoryIds(categoryIds)).thenReturn(listOf(product1.id!!, product2.id!!))
        `when`(productRepository.findProductsWithBrandAndCategory(listOf(product1.id!!, product2.id!!)))
            .thenReturn(listOf(product1, product2))

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertEquals(2, response.items.size)
        assertEquals(BigDecimal(3000), response.totalPrice)
    }

    @Test
    fun `카테고리는 존재하는데 해당 카테고리에 상품이 존재하지 않는 경우`() {
        // given
        val categoryIds = listOf(category1.id!!, category2.id!!)
        `when`(categoryService.findAllCategoryIds()).thenReturn(categoryIds)
        `when`(productRepository.findLowestPriceProductsByCategoryIds(categoryIds)).thenReturn(emptyList())

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertTrue(response.items.isEmpty())
        assertEquals(BigDecimal.ZERO, response.totalPrice)
    }

    @Test
    fun `ProductRepository에서 findLowestPriceProductsByCategoryIds 메서드가 null을 반환하는 경우`() {
        // given
        val categoryIds = listOf(category1.id!!)
        `when`(categoryService.findAllCategoryIds()).thenReturn(categoryIds)
        `when`(productRepository.findLowestPriceProductsByCategoryIds(categoryIds)).thenReturn(null)

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertTrue(response.items.isEmpty())
        assertEquals(BigDecimal.ZERO, response.totalPrice)
    }

    @Test
    fun `카테고리 서비스가 null을 반환하는 경우`() {
        // given
        `when`(categoryService.findAllCategoryIds()).thenReturn(null)

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertTrue(response.items.isEmpty())
        assertEquals(BigDecimal.ZERO, response.totalPrice)
    }


    private fun getCategory1() = MockCategoryBuilder(
        id = 1L,
        name = "Category1",
        createdAt = LocalDateTime.now().minusSeconds(3600),
        updatedAt = LocalDateTime.now().minusSeconds(3600)
    ).build()

    private fun getCategory2() = MockCategoryBuilder(
        id = 2L,
        name = "Category2",
        createdAt = LocalDateTime.now().minusSeconds(3600),
        updatedAt = LocalDateTime.now().minusSeconds(3600)
    ).build()

    private fun getBrand2() = BrandBuilder(
        id = 2L,
        name = "Brand2",
        createdAt = LocalDateTime.now().minusSeconds(3600),
        updatedAt = LocalDateTime.now().minusSeconds(3600)
    ).build()

    private fun getBrand1() = BrandBuilder(
        id = 1L,
        name = "Brand1",
        createdAt = LocalDateTime.now().minusSeconds(3600),
        updatedAt = LocalDateTime.now().minusSeconds(3600)
    ).build()
}

class MockProductBuilder(
    private val id: Long? = null,
    private val brand: Brand = Brand(name = "DefaultBrand"),
    private val category: Category = Category(name = "DefaultCategory"),
    private val price: BigDecimal = BigDecimal(0),
    private val createdAt: LocalDateTime = LocalDateTime.now(),
    private val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun build(): Product {
        val product = Product(brand, category, price)
        product.id = id
        product.createdAt = createdAt
        product.updatedAt = updatedAt
        return product
    }
}

class MockCategoryBuilder(
    private val id: Long? = null,
    private val name: String = "DefaultCategory",
    private val createdAt: LocalDateTime = LocalDateTime.now(),
    private val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun build(): Category {
        val category = Category(name = name)
        category.id = id
        category.createdAt = createdAt
        category.updatedAt = updatedAt
        return category
    }
}

class BrandBuilder(
    private val id: Long? = null,
    private val name: String = "DefaultBrand",
    private val createdAt: LocalDateTime = LocalDateTime.now(),
    private val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun build(): Brand {
        val brand = Brand(name = name)
        brand.id = id
        brand.createdAt = createdAt
        brand.updatedAt = updatedAt
        return brand
    }
}