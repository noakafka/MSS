package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.Brand
import com.mss.assignment.domain.brand.BrandService
import com.mss.assignment.domain.category.Category
import com.mss.assignment.domain.product.Product
import com.mss.assignment.domain.product.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class CoordinationServiceTest {

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

}
