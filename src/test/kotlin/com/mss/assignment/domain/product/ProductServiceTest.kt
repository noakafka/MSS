package com.mss.assignment.domain.product

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.category.CategoryRepository
import com.mss.assignment.domain.product.request.ProductRequest
import com.mss.assignment.exception.ErrorCode
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
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {
    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var brandRepository: BrandRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var productService: ProductService

    private lateinit var category: Category
    private lateinit var brand: Brand
    private lateinit var product: Product

    @BeforeEach
    fun setUp() {
        category = Category("Electronics")
        brand = Brand("BrandA")
        product = Product(brand, category, BigDecimal(1000))

        // 리플렉션을 사용하여 ID 설정
        setEntityId(category, 1L)
        setEntityId(brand, 1L)
        setEntityId(product, 1L)
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
    fun `Product 생성 시 brand와 category가 유효한지 확인`() {
        // given
        val productDto = ProductRequest(brand.name, category.name, BigDecimal(1000))
        `when`(brandRepository.findByName(brand.name)).thenReturn(Optional.of(brand))
        `when`(categoryRepository.findByName(category.name)).thenReturn(Optional.of(category))
        `when`(productRepository.save(any())).thenReturn(product)

        // when
        val createdProduct = productService.createProduct(productDto)

        // then
        assertNotNull(createdProduct)
        assertEquals(brand.name, createdProduct.brandName)
        assertEquals(category.name, createdProduct.categoryName)
        assertEquals(productDto.price.toInt(), createdProduct.price)
    }

    @Test
    fun `존재하지 않는 brand로 Product 생성 시 예외 발생`() {
        // given
        val productDto = ProductRequest(brand.name, category.name, BigDecimal(1500))
        `when`(brandRepository.findByName(brand.name)).thenReturn(Optional.empty())

        // when & then
        val exception =
            assertThrows<NotFoundException> {
                productService.createProduct(productDto)
            }
        assertEquals(ErrorCode.BRAND_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `존재하지 않는 Product 업데이트 시 예외 발생`() {
        // given
        val productDto = ProductRequest(brand.name, category.name, BigDecimal(1500))
        `when`(productRepository.findById(product.id)).thenReturn(Optional.empty())

        // when & then
        val exception =
            assertThrows<NotFoundException> {
                productService.updateProduct(product.id, productDto)
            }
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `Product 삭제 시 존재 여부 확인`() {
        // given
        `when`(productRepository.existsById(product.id)).thenReturn(true)
        doNothing().`when`(productRepository).deleteById(product.id)

        // when
        productService.deleteProduct(product.id)

        // then
        verify(productRepository, times(1)).deleteById(product.id)
    }

    @Test
    fun `존재하지 않는 Product 삭제 시 예외 발생`() {
        // given
        `when`(productRepository.existsById(product.id)).thenReturn(false)

        // when & then
        val exception =
            assertThrows<NotFoundException> {
                productService.deleteProduct(product.id)
            }
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.errorCode)
    }
}
