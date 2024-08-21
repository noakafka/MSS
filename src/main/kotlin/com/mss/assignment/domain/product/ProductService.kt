package com.mss.assignment.domain.product

import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.category.CategoryRepository
import com.mss.assignment.domain.product.request.ProductRequest
import com.mss.assignment.dto.ProductDto
import jakarta.persistence.EntityNotFoundException
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
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory"], allEntries = true)
    fun createProduct(productDto: ProductRequest): ProductDto {
        val brand = brandRepository.findById(productDto.brandId)
            .orElseThrow { IllegalArgumentException("Invalid brand ID") }
        val category = categoryRepository.findById(productDto.categoryId)
            .orElseThrow { IllegalArgumentException("Invalid category ID") }

        val product = Product(
            brand = brand,
            category = category,
            price = productDto.price
        )

        return ProductDto.fromEntity(productRepository.save(product))
    }

    @Transactional
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory"], allEntries = true)
    fun updateProduct(id: Long, productDto: ProductRequest): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product not found with ID: $id") }

        val brand = brandRepository.findById(productDto.brandId)
            .orElseThrow { IllegalArgumentException("Invalid brand ID") }
        val category = categoryRepository.findById(productDto.categoryId)
            .orElseThrow { IllegalArgumentException("Invalid category ID") }

        product.update(
            brand = brand,
            category = category,
            price = productDto.price
        )

        return ProductDto.fromEntity(productRepository.save(product))
    }

    @Transactional
    @CacheEvict(cacheNames = ["lowestPriceProductsByCategory", "priceSummaryForCategory"], allEntries = true)
    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id)) {
            throw EntityNotFoundException("Product not found with ID: $id")
        }
        productRepository.deleteById(id)
    }
}
