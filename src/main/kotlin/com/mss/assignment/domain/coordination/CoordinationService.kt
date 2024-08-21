package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.category.CategoryService
import com.mss.assignment.domain.product.Product
import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.dto.ProductDto
import com.mss.assignment.dto.LowestPriceResponse
import com.mss.assignment.dto.PriceSummary
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CoordinationService(
    private val categoryService: CategoryService,
    private val productRepository: ProductRepository,
) {
    @Cacheable("lowestPriceProductsByCategory")
    fun findLowestPriceProductsByCategory(): LowestPriceResponse {
        val allCategories = categoryService.findAllCategoryIds()
        val latestProductIdsByCategory = productRepository.findLowestPriceProductsByCategoryIds(allCategories)

        // for n+1 problem
        val products = productRepository.findProductsWithBrandAndCategory(latestProductIdsByCategory)

        return createLowestPriceResponse(products)
    }

    private fun createLowestPriceResponse(products: List<Product>): LowestPriceResponse {
        val items = products.map { product ->
            ProductDto(
                categoryName = product.category.name,
                brandName = product.brand.name,
                price = product.price
            )
        }

        val totalPrice = items.sumOf { it.price }
        return LowestPriceResponse(
            items = items,
            totalPrice = totalPrice
        )
    }

    @Cacheable("priceSummaryForCategory")
    fun getPriceSummaryForCategory(categoryName: String): PriceSummary {
        val minPrice = productRepository.findMinPriceByCategoryName(categoryName)
        val minPriceProducts = productRepository.findProductsByCategoryNameAndPrice(categoryName, minPrice)

        val maxPrice = productRepository.findMaxPriceByCategoryName(categoryName)
        val maxPriceProducts = productRepository.findProductsByCategoryNameAndPrice(categoryName, maxPrice)

        return PriceSummary(
            category = categoryName,
            minPrice = minPriceProducts,
            maxPrice = maxPriceProducts
        )
    }
}