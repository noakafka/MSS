package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.product.Product
import com.mss.assignment.domain.product.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.math.BigDecimal
import java.util.*
import kotlin.NoSuchElementException

@ExtendWith(MockitoExtension::class)
class CoordinationServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var brabdRepository: BrandRepository

    @InjectMocks
    private lateinit var coordinationService: CoordinationService

    private lateinit var category1: Category
    private lateinit var category2: Category
    private lateinit var brand1: Brand
    private lateinit var brand2: Brand
    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var product3: Product

    @BeforeEach
    fun setUp() {
        category1 = Category("Category1")
        category2 = Category("Category2")
        brand1 = Brand("Brand1")
        brand2 = Brand("Brand2")

        product1 = Product(brand1, category1, BigDecimal(1000))
        product2 = Product(brand2, category2, BigDecimal(2000))
        product3 = Product(brand1, category1, BigDecimal(3000))

        // 리플렉션을 사용하여 ID 설정
        setEntityId(product1, 1L)
        setEntityId(product2, 2L)
        setEntityId(product3, 3L)
    }

    private fun <T : Any> setEntityId(entity: T, id: Long) {
        val idField = entity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }

    /**
     * Task 1. 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
     * CoordinationService.findCheapestEachCategory
     */
    @Test
    fun `repository에서 조회 결과가 빈 리스트 일때 - 에러를 반환`() {
        // given
        given(productRepository.findCheapestByCategoryOrderByCategory()).willReturn(emptyList())

        // when
        val result = assertThrows<NoSuchElementException> {
            coordinationService.findCheapestEachCategory()
        }

        // then
        assertThat(result.message).isEqualTo("No product found")
    }

    @Test
    fun `findCheapestEachCategory -정상 조회`() {
        // given
        given(productRepository.findCheapestByCategoryOrderByCategory()).willReturn(listOf(product1, product2))

        // when
        val result = coordinationService.findCheapestEachCategory()

        // then
        assertThat(result.items).hasSize(2)
        assertThat(result.items[0].brandName).isEqualTo(brand1.name)
        assertThat(result.totalPrice).isEqualTo(BigDecimal(3000))
    }

    /**
     * Task 2. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
     * CoordinationService.findCheapestCoordinationByBrand
     */

    @Test
    fun `브랜드 조회 결과가 없을 때 - 에러를 반환`() {
        // given
        given(brabdRepository.findCheapestBrand()).willReturn(Optional.empty())

        // when
        val result = assertThrows<NoSuchElementException> {
            coordinationService.findCheapestCoordinationByBrand()
        }

        // then
        assertThat(result.message).isEqualTo("No brand found")
    }

    @Test
    fun `findCheapestCoordinationByBrand - 정상 조회`() {
        // given
        given(brabdRepository.findCheapestBrand()).willReturn(Optional.of(brand1))
        given(productRepository.findCheapestCoordinationByBrands(brand1.id)).willReturn(listOf(product1))

        // when
        val result = coordinationService.findCheapestCoordinationByBrand()

        // then
        assertThat(result.products).hasSize(1)
        assertThat(result.brandName).isEqualTo(brand1.name)
        assertThat(result.totalPrice).isEqualTo(BigDecimal(1000))
    }

    /**
     * Task 3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     * CoordinationService.getCheapestAndMostExpensiveByCategory
     */
    @Test
    fun `카테고리 이름으로 최저가 상품 조회 결과가 없을 때 - 에러를 반환`() {
        // given
        given(productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc("Category1")).willReturn(Optional.empty())

        // when
        val result = assertThrows<NoSuchElementException> {
            coordinationService.getCheapestAndMostExpensiveByCategory("Category1")
        }

        // then
        assertThat(result.message).isEqualTo("No product found for category")
    }

    @Test
    fun `정상 조회 - 최저가, 최대가 상품이 같을 때`() {
        // given
        given(productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc(category2.name)).willReturn(Optional.of(product2))
        given(productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc(category2.name)).willReturn(Optional.of(product2))

        // when
        val result = coordinationService.getCheapestAndMostExpensiveByCategory(category2.name)

        // then
        assertThat(result.categoryName).isEqualTo(category2.name)
        assertThat(result.cheapestProduct.brandName).isEqualTo(brand2.name)
        assertThat(result.cheapestProduct.price).isEqualTo(BigDecimal(2000))
        assertThat(result.mostExpensiveProduct.brandName).isEqualTo(brand2.name)
        assertThat(result.mostExpensiveProduct.price).isEqualTo(BigDecimal(2000))
    }

    @Test
    fun `정상 조회 - 최저가, 최대가 상품이 다를 떄`() {
        // given
        given(productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc(category1.name)).willReturn(Optional.of(product1))
        given(productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc(category1.name)).willReturn(Optional.of(product3))

        // when
        val result = coordinationService.getCheapestAndMostExpensiveByCategory(category1.name)

        // then
        assertThat(result.categoryName).isEqualTo(category1.name)
        assertThat(result.cheapestProduct.brandName).isEqualTo(brand1.name)
        assertThat(result.cheapestProduct.price).isEqualTo(BigDecimal(1000))
        assertThat(result.mostExpensiveProduct.brandName).isEqualTo(brand1.name)
        assertThat(result.mostExpensiveProduct.price).isEqualTo(BigDecimal(3000))
    }

}
