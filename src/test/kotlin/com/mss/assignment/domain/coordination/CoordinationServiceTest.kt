package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandService
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.category.CategoryService
import com.mss.assignment.domain.product.Product
import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.dto.MinMaxPrice
import com.mss.assignment.dto.PriceSummary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class CoordinationServiceTest {

    @Mock
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var brandService: BrandService

    @InjectMocks
    private lateinit var coordinationService: CoordinationService

    private lateinit var category1: Category
    private lateinit var category2: Category
    private lateinit var brand1: Brand
    private lateinit var brand2: Brand
    private lateinit var product1: Product
    private lateinit var product2: Product

    @BeforeEach
    fun setUp() {
        category1 = Category("Category1")
        category2 = Category("Category2")
        brand1 = Brand("Brand1")
        brand2 = Brand("Brand2")

        product1 = Product(brand1, category1, BigDecimal(1000))
        product2 = Product(brand2, category2, BigDecimal(2000))

        // 리플렉션을 사용하여 ID 설정
        setEntityId(product1, 1L)
        setEntityId(product2, 2L)
    }

    private fun <T : Any> setEntityId(entity: T, id: Long) {
        val idField = entity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }

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
        val categoryIds = listOf(category1.id, category2.id)
        `when`(categoryService.findAllCategoryIds()).thenReturn(categoryIds)
        `when`(productRepository.findLowestPriceProductsByCategoryIds(categoryIds)).thenReturn(listOf(product1.id, product2.id))
        `when`(productRepository.findProductsWithBrandAndCategory(listOf(product1.id, product2.id))).thenReturn(listOf(product1, product2))

        // when
        val response = coordinationService.findLowestPriceProductsByCategory()

        // then
        assertEquals(2, response.items.size)
        assertEquals(BigDecimal(3000), response.totalPrice)
    }

    @Test
    fun `카테고리는 존재하는데 해당 카테고리에 상품이 존재하지 않는 경우`() {
        // given
        val categoryIds = listOf(category1.id, category2.id)
        println(categoryIds)
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
        val categoryIds = listOf(category1.id)
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

    @Test
    fun `특정 카테고리의 가격 요약을 올바르게 반환`() {
        // given
        val categoryName = "Category1"
        val minPrice = BigDecimal(1000)
        val maxPrice = BigDecimal(2000)

        val minPriceProducts = listOf(
            PriceSummary.ProductWithBrandAndPrice(brand1.name, minPrice)
        )

        val maxPriceProducts = listOf(
            PriceSummary.ProductWithBrandAndPrice(brand2.name, maxPrice)
        )

        `when`(productRepository.findMinMaxPriceByCategoryName(categoryName)).thenReturn(MinMaxPrice(minPrice, maxPrice))
        `when`(productRepository.findProductsByCategoryNameAndPrice(categoryName, minPrice)).thenReturn(minPriceProducts)

        `when`(productRepository.findProductsByCategoryNameAndPrice(categoryName, maxPrice)).thenReturn(maxPriceProducts)

        // when
        val result = coordinationService.getPriceSummaryForCategory(categoryName)

        // then
        assertNotNull(result)
        assertEquals(categoryName, result.category)
        assertEquals(minPriceProducts, result.minPrice)
        assertEquals(maxPriceProducts, result.maxPrice)
    }

    @Test
    fun `카테고리에 상품이 없는 경우 빈 리스트를 반환`() {
        // given
        val categoryName = "Category1"
        val minPrice = BigDecimal.ZERO
        val maxPrice = BigDecimal.ZERO

        `when`(productRepository.findMinMaxPriceByCategoryName(categoryName)).thenReturn(MinMaxPrice(minPrice, maxPrice))
        `when`(productRepository.findProductsByCategoryNameAndPrice(categoryName, minPrice)).thenReturn(emptyList())
        `when`(productRepository.findProductsByCategoryNameAndPrice(categoryName, maxPrice)).thenReturn(emptyList())

        // when
        val result = coordinationService.getPriceSummaryForCategory(categoryName)

        // then
        assertNotNull(result)
        assertEquals(categoryName, result.category)
        assertTrue(result.minPrice.isEmpty())
        assertTrue(result.maxPrice.isEmpty())
    }
}
