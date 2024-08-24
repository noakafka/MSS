package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.coordination.response.CheapestCoordinationByBrand
import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory.ProductWithBrandAndPrice
import com.mss.assignment.dto.CheapestEachCategory
import com.mss.assignment.dto.ProductDto
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CoordinationService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
) {
    @Cacheable("lowestPriceProductsByCategory")
    fun findCheapestEachCategory(): CheapestEachCategory {
        val productDtoList = productRepository.findCheapestByCategoryOrderByCategory().map {
            ProductDto.fromEntity(it)
        }
        return CheapestEachCategory.fromProductDtoList(productDtoList)
    }


    @Cacheable("priceSummaryForCategory")
    fun getCheapestAndMostExpensiveByCategory(categoryName: String): CheapestAndMostExpensiveByCategory {
        val cheapestProduct = productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc(categoryName).getOrElse { throw NoSuchElementException("No product found for category $categoryName") }
        val mostExpensiveProduct = productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc(categoryName).getOrElse { cheapestProduct }

        return CheapestAndMostExpensiveByCategory(
            category = categoryName,
            cheapestProduct = ProductWithBrandAndPrice(
                brandName = cheapestProduct.brand.name,
                price = cheapestProduct.price
            ),
            mostExpensiveProduct = ProductWithBrandAndPrice(
                brandName = mostExpensiveProduct.brand.name,
                price = mostExpensiveProduct.price
            ),
        )
    }

    @Cacheable("cheapestCoordinationByBrand")
    fun findCheapestCoordinationByBrand(): CheapestCoordinationByBrand {
        val brand = brandRepository.findCheapestBrand()
        return  CheapestCoordinationByBrand.fromProductList(productRepository.findCheapestCoordinationByBrands(brand.id))
    }
}
