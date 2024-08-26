package com.mss.assignment.integration

import com.mss.assignment.domain.coordination.response.CheapestCoordinationByBrand
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory
import com.mss.assignment.dto.CheapestEachCategory
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.ErrorResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CoordinationIntegrationTest {
    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Autowired
    private val messageSource: MessageSource? = null

    @Test
    fun `카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API - 정상`() {
        // CoordinationController의 /lowest-price-by-category 엔드포인트 테스트
        val response =
            restTemplate!!.getForEntity(
                "/coordinations/lowest-price-by-category",
                CheapestEachCategory::class.java,
            )

        val cheapestEachCategory = response.body
        assertThat(cheapestEachCategory!!.totalPrice).isEqualTo(34100)

        val items = cheapestEachCategory.items
        assertThat(items).hasSize(8)

        // 각 항목의 세부 내용 확인
        assertThat(items).extracting("id").containsExactly(17L, 34L, 27L, 52L, 5L, 30L, 71L, 48L)
        assertThat(items).extracting("categoryName")
            .containsExactly("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리")
        assertThat(items).extracting("brandName")
            .containsExactly("C", "E", "D", "G", "A", "D", "I", "F")
        assertThat(items).extracting("price")
            .containsExactly(10000, 5000, 3000, 9000, 2000, 1500, 1700, 1900)
    }

    @Test
    fun `단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을조회하는 API - 정상`() {
        // CoordinationController의 /cheapest-coordination-by-brand 엔드포인트 테스트
        val response: ResponseEntity<CheapestCoordinationByBrand> =
            restTemplate!!.getForEntity(
                "/coordinations/cheapest-coordination-by-brand",
                CheapestCoordinationByBrand::class.java,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.brandName).isEqualTo("D")
        assertThat(result.totalPrice).isEqualTo(36100)

        // Products 리스트 검증
        val products = result.products
        assertThat(products).hasSize(8)

        // 개별 상품의 세부 내용 검증
        assertThat(products[0].categoryName).isEqualTo("상의")
        assertThat(products[0].price).isEqualTo(10100)

        assertThat(products[1].categoryName).isEqualTo("아우터")
        assertThat(products[1].price).isEqualTo(5100)

        assertThat(products[2].categoryName).isEqualTo("바지")
        assertThat(products[2].price).isEqualTo(3000)

        assertThat(products[3].categoryName).isEqualTo("스니커즈")
        assertThat(products[3].price).isEqualTo(9500)

        assertThat(products[4].categoryName).isEqualTo("가방")
        assertThat(products[4].price).isEqualTo(2500)

        assertThat(products[5].categoryName).isEqualTo("모자")
        assertThat(products[5].price).isEqualTo(1500)

        assertThat(products[6].categoryName).isEqualTo("양말")
        assertThat(products[6].price).isEqualTo(2400)

        assertThat(products[7].categoryName).isEqualTo("액세서리")
        assertThat(products[7].price).isEqualTo(2000)
    }

    @Test
    fun `상의 카테고리로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API - 정상`() {
        // CoordinationController의 /price-summary-by-category 엔드포인트 테스트
        val category = "상의"
        val response =
            restTemplate!!.getForEntity(
                "/coordinations/price-summary-by-category?category=$category",
                CheapestAndMostExpensiveByCategory::class.java,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.categoryName).isEqualTo(category)

        // CheapestProduct 검증
        val cheapestProduct = result.cheapestProduct
        assertThat(cheapestProduct.brandName).isEqualTo("C")
        assertThat(cheapestProduct.price).isEqualTo(10000)

        // MostExpensiveProduct 검증
        val mostExpensiveProduct = result.mostExpensiveProduct
        assertThat(mostExpensiveProduct.brandName).isEqualTo("I")
        assertThat(mostExpensiveProduct.price).isEqualTo(11400)
    }

    @Test
    fun `존재하지 않는 카테고리로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API - 실패`() {
        // CoordinationController의 /price-summary-by-category 엔드포인트 테스트
        val category = "축구화"
        val response =
            restTemplate!!.getForEntity(
                "/coordinations/price-summary-by-category?category=$category",
                ErrorResponse::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNotNull
        val result = response.body!!
        assertThat(result.status).isEqualTo(404)
        assertThat(result.message).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage(messageSource!!))
    }
}
