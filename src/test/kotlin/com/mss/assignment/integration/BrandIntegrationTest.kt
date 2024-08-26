package com.mss.assignment.integration

import com.mss.assignment.domain.brand.request.CreateBrandRequest
import com.mss.assignment.domain.brand.request.UpdateBrandRequest
import com.mss.assignment.dto.BrandDto
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BrandIntegrationTest {
    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Autowired
    private val messageSource: MessageSource? = null

    @Test
    fun `브랜드 생성 - 성공`() {
        val brandName = "ALPHA"
        val response =
            restTemplate!!.postForEntity(
                "/brands",
                CreateBrandRequest(brandName),
                BrandDto::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.name).isEqualTo(brandName)
    }

    @Test
    fun `중복 이름으로 브랜드 생성 - 실패`() {
        val brandName = "D"
        val response =
            restTemplate!!.postForEntity(
                "/brands",
                CreateBrandRequest(brandName),
                ErrorResponse::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.status).isEqualTo(409)
        assertThat(result.message).isEqualTo(ErrorCode.NAME_CONFLICT.getMessage(messageSource!!))
    }

    @Test
    fun `브랜드 업데이트 - 성공`() {
        // 브랜드 생성
        val createBrandName = "POLAR"
        val createdResponse =
            restTemplate!!.postForEntity(
                "/brands",
                CreateBrandRequest(createBrandName),
                BrandDto::class.java,
            )
        // 브랜드 업데이트
        val brandId = createdResponse.body!!.id
        val newBrandName = "ACE"
        val request = UpdateBrandRequest(newBrandName)
        val entity = HttpEntity(request)
        val response =
            restTemplate.exchange(
                "/brands/{id}",
                HttpMethod.PUT,
                entity,
                BrandDto::class.java,
                brandId,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.name).isEqualTo(newBrandName)
    }

    @Test
    fun `중복 이름으로 브랜드 업데이트 - 실패`() {
        val brandId = 1L
        val newBrandName = "B"
        val request = UpdateBrandRequest(newBrandName)
        val entity = HttpEntity(request)
        val response =
            restTemplate!!.exchange(
                "/brands/{id}",
                HttpMethod.PUT,
                entity,
                ErrorResponse::class.java,
                brandId,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.message).isEqualTo(ErrorCode.NAME_CONFLICT.getMessage(messageSource!!))
    }

    @Test
    fun `상품을 가지고 있지 않는 브랜드 삭제 - 성공`() {
        val newBrandName = "POLAR"
        val createdResponse =
            restTemplate!!.postForEntity(
                "/brands",
                CreateBrandRequest(newBrandName),
                BrandDto::class.java,
            )

        // BrandController 의 DELETE 엔드포인트 테스트
        val brandId = createdResponse.body!!.id
        val response =
            restTemplate.exchange(
                "/brands/{id}",
                HttpMethod.DELETE,
                null,
                Void::class.java,
                brandId,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `상품을 가지고 있는 브랜드 삭제 - 실패`() {
        val brandId = 1L
        val response =
            restTemplate!!.exchange(
                "/brands/{id}",
                HttpMethod.DELETE,
                null,
                ErrorResponse::class.java,
                brandId,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.message).isEqualTo(ErrorCode.BRAND_HAS_PRODUCTS.getMessage(messageSource!!))
    }

    @Test
    fun `존재하지 않는 브랜드 삭제 - 실패`() {
        val brandId = 0
        val response =
            restTemplate!!.exchange(
                "/brands/{id}",
                HttpMethod.DELETE,
                null,
                ErrorResponse::class.java,
                brandId,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNotNull

        val result = response.body!!
        assertThat(result.message).isEqualTo(ErrorCode.BRAND_NOT_FOUND.getMessage(messageSource!!))
    }
}
