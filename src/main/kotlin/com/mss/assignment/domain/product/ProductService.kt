package com.mss.assignment.domain.product

import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.CategoryRepository
import com.mss.assignment.domain.product.request.ProductRequest
import com.mss.assignment.dto.ProductDto
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val categoryRepository: CategoryRepository
) {

    @Transactional
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory", "cheapestCoordinationByBrand"], allEntries = true)
    fun createProduct(productDto: ProductRequest): ProductDto {
        val brand = brandRepository.findById(productDto.brandId)
            .orElseThrow { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
        val category = categoryRepository.findById(productDto.categoryId)
            .orElseThrow { NotFoundException(ErrorCode.CATEGORY_NOT_FOUND) }

        val product = Product(
            brand = brand,
            category = category,
            price = productDto.price
        )

        return ProductDto.fromEntity(productRepository.save(product))
    }

    @Transactional
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory", "cheapestCoordinationByBrand"], allEntries = true)
    fun updateProduct(id: Long, productDto: ProductRequest): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorCode.PRODUCT_NOT_FOUND) }
        val brand = brandRepository.findById(productDto.brandId)
            .orElseThrow { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
        val category = categoryRepository.findById(productDto.categoryId)
            .orElseThrow { NotFoundException(ErrorCode.CATEGORY_NOT_FOUND) }

        product.update(
            brand = brand,
            category = category,
            price = productDto.price
        )

        return ProductDto.fromEntity(productRepository.save(product))
    }

    @Transactional
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory", "cheapestCoordinationByBrand"], allEntries = true)
    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id)) { throw NotFoundException(ErrorCode.PRODUCT_NOT_FOUND) }
        productRepository.deleteById(id)
    }
}
