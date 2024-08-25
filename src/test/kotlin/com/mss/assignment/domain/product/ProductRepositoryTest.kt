package com.mss.assignment.domain.product

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.category.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

@ActiveProfiles("test")
@DataJpaTest
class ProductRepositoryTest(
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val brandRepository: BrandRepository,
) {
    /**
     * Task 1 : 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
     * ProductRepository.findCheapestByCategoryOrderByCategory 테스트
     */
    @Test
    fun `findCheapestByCategoryOrderByCategory - 한 카테고리 내에 최저가 상품이 여러 개 존재하는 경우 updatedAt이 가장 최신인 하나의 상품만 반환`() {
        // given
        val category = categoryRepository.save(Category(name = "CategoryA"))
        val brand = brandRepository.save(Brand(name = "BrandA"))

        // 동일한 카테고리에 동일한 가격을 가진 두 개의 상품 저장
        val product1 = productRepository.save(Product(brand = brand, category = category, price = BigDecimal(1000)))
        val product2 = productRepository.save(Product(brand = brand, category = category, price = BigDecimal(1000)))

        // when
        val result = productRepository.findCheapestByCategoryOrderByCategory()

        // then
        assertEquals(1, result.size)
        assertThat(result.first().id).isEqualTo(product2.id) // 둘 중 늦게 저장된 상품이 반환되어야 함
    }

    @Test
    fun `findCheapestByCategoryOrderByCategory - 카테고리별로 최저가 상품을 정확하게 반환`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val category2 = categoryRepository.save(Category(name = "Category2"))
        val brand1 = brandRepository.save(Brand(name = "BrandA"))
        val brand2 = brandRepository.save(Brand(name = "BrandB"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(5000)))
        val product2 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(300)))
        val product3 = productRepository.save(Product(brand = brand2, category = category2, price = BigDecimal(7000)))
        val product4 = productRepository.save(Product(brand = brand2, category = category2, price = BigDecimal(400)))

        // when
        val result = productRepository.findCheapestByCategoryOrderByCategory()

        // then
        assertEquals(2, result.size)
        assertThat(result[0].id).isEqualTo(product2.id) // Category1 카테고리의 최저가 상품
        assertThat(result[1].id).isEqualTo(product4.id) // Category2 카테고리의 최저가 상품
    }

    @Test
    fun `findCheapestByCategoryOrderByCategory - 상품이 없는 Category가 있을 때, 상품이 존재하는 카테고리의 반환값만 나온다`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val category2 = categoryRepository.save(Category(name = "Category2"))
        val brand1 = brandRepository.save(Brand(name = "BrandA"))
        val brand2 = brandRepository.save(Brand(name = "BrandB"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(5000)))
        val product2 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(300)))

        // when
        val result = productRepository.findCheapestByCategoryOrderByCategory()

        // then
        assertEquals(1, result.size)
        assertThat(result[0].id).isEqualTo(product2.id) // Category1 카테고리의 최저가 상품
    }

    @Test
    fun `findCheapestByCategoryOrderByCategory - 상품이 없을 때, 빈 리스트가 반환된다`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val brand1 = brandRepository.save(Brand(name = "BrandA"))

        // when
        val result = productRepository.findCheapestByCategoryOrderByCategory()

        // then
        assertEquals(0, result.size)
    }

    /**
     * Task 2 : 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
     * ProductRepository.findCheapestByBrandOrderByBrand 테스트
     */
    @Test
    fun `합계가 같은 제일 저렴한 두 개의 브랜드가 존재하는 경우 - 제일 최근에 업데이트된 브랜드를 선택한다`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val brand1 = brandRepository.save(Brand(name = "Brand1"))
        val brand2 = brandRepository.save(Brand(name = "Brand2"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(5000)))
        val product2 = productRepository.save(Product(brand = brand2, category = category1, price = BigDecimal(2000)))
        val product3 = productRepository.save(Product(brand = brand2, category = category1, price = BigDecimal(5000)))
        val product4 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(2000)))

        // when
        val cheapestBrand = brandRepository.findCheapestBrand().getOrNull()!!
        val result = productRepository.findCheapestCoordinationByBrands(cheapestBrand.id)

        // then
        assertEquals(1, result.size)
        assertThat(result[0].brand.id).isEqualTo(brand2.id)
    }

    /**
     * Task 3 : 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     * ProductRepository.findMinMaxPriceByCategoryName 테스트
     */
    @Test
    fun `카테고리에 상품이 하나만 있을 경우, 하나의 상품이 조회된다`() {
        // given
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val brand1 = brandRepository.save(Brand(name = "Brand1"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(5000)))

        // when
        val cheapestProduct = productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc("Category1").getOrNull()!!
        val mostExpensiveProduct = productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc("Category1").getOrNull()!!

        // then
        assertThat(cheapestProduct.id).isEqualTo(product1.id)
        assertThat(mostExpensiveProduct.id).isEqualTo(product1.id)
    }

    @Test
    fun `상품이 두 개 있는데 카테고리와 가격이 같을 경우, 최저가, 최고가 모두 제일 최근에 업데이트된 상품이 조회된다`() {
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val brand1 = brandRepository.save(Brand(name = "Brand1"))
        val brand2 = brandRepository.save(Brand(name = "Brand2"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(5000)))
        val product2 = productRepository.save(Product(brand = brand2, category = category1, price = BigDecimal(5000)))

        // when
        val cheapestProduct = productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc("Category1").getOrNull()!!
        val mostExpensiveProduct = productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc("Category1").getOrNull()!!

        // then
        assertThat(cheapestProduct.id).isEqualTo(product2.id)
        assertThat(mostExpensiveProduct.id).isEqualTo(product2.id)
    }

    @Test
    fun `카테고리에 상품이 두 개 있는데 가격이 다를 경우, 최저가, 최고가 모두 정확하게 조회된다`() {
        val category1 = categoryRepository.save(Category(name = "Category1"))
        val brand1 = brandRepository.save(Brand(name = "Brand1"))
        val brand2 = brandRepository.save(Brand(name = "Brand2"))

        val product1 = productRepository.save(Product(brand = brand1, category = category1, price = BigDecimal(500)))
        val product2 = productRepository.save(Product(brand = brand2, category = category1, price = BigDecimal(9000)))

        // when
        val cheapestProduct = productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc("Category1").getOrNull()!!
        val mostExpensiveProduct = productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc("Category1").getOrNull()!!

        // then
        assertThat(cheapestProduct.id).isEqualTo(product1.id)
        assertThat(mostExpensiveProduct.id).isEqualTo(product2.id)
    }
}
