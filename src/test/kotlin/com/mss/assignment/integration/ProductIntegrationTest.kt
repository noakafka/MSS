package com.mss.assignment.integration

import com.mss.assignment.domain.product.request.ProductRequest
import com.mss.assignment.dto.ProductDto
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.ErrorResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.MessageSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ProductIntegrationTest {
    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Autowired
    private val messageSource: MessageSource? = null

    @Test
    fun `상품 생성 - 성공`() {
        // product 생성
        val brandName = "A"
        val categoryName = "모자"
        val response =
            restTemplate!!.postForEntity(
                "/products",
                ProductRequest(brandName, categoryName, BigDecimal(1000000)),
                ProductDto::class.java,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.brandName).isEqualTo(brandName)
        assertThat(result.categoryName).isEqualTo(categoryName)
        assertThat(result.price).isEqualTo(1000000)
    }

    @Test
    fun `상품 업데이트 - 성공`() {
        // product 생성
        val brandName = "A"
        val categoryName = "모자"
        val response =
            restTemplate!!.postForEntity(
                "/products",
                ProductRequest(brandName, categoryName, BigDecimal(1000000)),
                ProductDto::class.java,
            )
        val result = response.body!!

        // product 업데이트
        val requestEntity = HttpEntity(ProductRequest(brandName, categoryName, BigDecimal(2000000)))
        val updateResponse =
            restTemplate.exchange(
                "/products/{id}",
                HttpMethod.PUT,
                requestEntity,
                ProductDto::class.java,
                result.id,
            )

        assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(updateResponse.body).isNotNull

        val updateResult = updateResponse.body!!
        assertThat(updateResult.brandName).isEqualTo(brandName)
        assertThat(updateResult.categoryName).isEqualTo(categoryName)
        assertThat(updateResult.price).isEqualTo(2000000)
    }

    @Test
    fun `상품 삭제 - 성공`() {
        // product 생성
        val brandName = "A"
        val categoryName = "모자"
        val createResponse =
            restTemplate!!.postForEntity(
                "/products",
                ProductRequest(brandName, categoryName, BigDecimal(1000000)),
                ProductDto::class.java,
            )
        val result = createResponse.body!!

        // product 삭제
        val response =
            restTemplate.exchange(
                "/products/{id}",
                HttpMethod.DELETE,
                null,
                Void::class.java,
                result.id,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `존재하지 않는 상품 삭제 - 실패`() {
        val productId = 0L
        val response =
            restTemplate!!.exchange(
                "/products/{id}",
                HttpMethod.DELETE,
                null,
                ErrorResponse::class.java,
                productId,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.message).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage(messageSource!!))
    }
}
