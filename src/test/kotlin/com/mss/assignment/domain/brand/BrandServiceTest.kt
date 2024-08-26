package com.mss.assignment.domain.brand

import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.GlobalHttpException
import com.mss.assignment.exception.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BrandServiceTest {
    @Mock
    private lateinit var brandRepository: BrandRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var brandService: BrandService

    private lateinit var brand: Brand

    @BeforeEach
    fun setUp() {
        brand = Brand("BrandA")
        setEntityId(brand, 1L)
    }

    private fun <T : Any> setEntityId(
        entity: T,
        id: Long,
    ) {
        val idField = entity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }

    @Test
    fun `Brand 생성 시 중복 이름이 있으면 예외 발생`() {
        // given
        `when`(brandRepository.existsByName(brand.name)).thenReturn(true)

        // when & then
        val exception =
            assertThrows<GlobalHttpException> {
                brandService.createBrand(brand.name)
            }
        assertEquals(ErrorCode.NAME_CONFLICT, exception.errorCode)
    }

    @Test
    fun `Brand 성공적으로 생성`() {
        // given
        `when`(brandRepository.existsByName(brand.name)).thenReturn(false)
        `when`(brandRepository.save(any())).thenReturn(brand)

        // when
        val createdBrand = brandService.createBrand(brand.name)

        // then
        assertNotNull(createdBrand)
        assertEquals(brand.name, createdBrand.name)
    }

    @Test
    fun `Brand 업데이트 시 중복 이름이 있으면 예외 발생`() {
        // given
        `when`(brandRepository.existsByName(brand.name)).thenReturn(true)
        `when`(brandRepository.findById(brand.id)).thenReturn(Optional.of(brand))

        // when & then
        val exception =
            assertThrows<GlobalHttpException> {
                brandService.updateBrand(brand.id, brand.name)
            }
        assertEquals(ErrorCode.NAME_CONFLICT, exception.errorCode)
    }

    @Test
    fun `존재하지 않는 Brand 삭제 시 예외 발생`() {
        // given
        `when`(brandRepository.existsById(brand.id)).thenReturn(false)

        // when & then
        val exception =
            assertThrows<NotFoundException> {
                brandService.deleteBrand(brand.id)
            }
        assertEquals(ErrorCode.BRAND_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `Brand 성공적으로 삭제`() {
        // given
        `when`(brandRepository.existsById(brand.id)).thenReturn(true)
        `when`(productRepository.existsByBrandId(brand.id)).thenReturn(false)
        doNothing().`when`(brandRepository).deleteById(brand.id)

        // when
        brandService.deleteBrand(brand.id)

        // then
        verify(brandRepository, times(1)).deleteById(brand.id)
    }

    @Test
    fun `상품이 존재하는 Brand 삭제 실패`() {
        // given
        `when`(brandRepository.existsById(brand.id)).thenReturn(true)
        `when`(productRepository.existsByBrandId(brand.id)).thenReturn(true)

        // when
        val exception =
            assertThrows<GlobalHttpException> {
                brandService.deleteBrand(brand.id)
            }
        assertEquals(ErrorCode.BRAND_HAS_PRODUCTS, exception.errorCode)

        // then
        verify(brandRepository, times(0)).deleteById(brand.id)
    }
}
