package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.brand.BrandRepository
import com.mss.assignment.domain.coordination.response.CheapestCoordinationByBrand
import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory.ProductWithBrandAndPrice
import com.mss.assignment.dto.CheapestEachCategory
import com.mss.assignment.dto.ProductDto
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.NotFoundException
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CoordinationService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
) {
    @Cacheable("cheapestProductEachCategory")
    fun findCheapestEachCategory(): CheapestEachCategory {
        val productDtoList =
            productRepository.findCheapestByCategoryOrderByCategory().map {
                ProductDto.fromEntity(it)
            }
        if (productDtoList.isEmpty()) {
            throw NotFoundException(ErrorCode.PRODUCT_NOT_FOUND)
        }
        return CheapestEachCategory.fromProductDtoList(productDtoList)
    }

    @Cacheable("cheapestCoordinationByBrand")
    fun findCheapestCoordinationByBrand(): CheapestCoordinationByBrand {
        val brand = brandRepository.findCheapestBrand().getOrElse { throw NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
        return CheapestCoordinationByBrand.fromProductList(productRepository.findCheapestCoordinationByBrands(brand.id))
    }

    @Cacheable("cheapestAndMostExpensiveByCategory")
    fun getCheapestAndMostExpensiveByCategory(categoryName: String): CheapestAndMostExpensiveByCategory {
        val cheapestProduct =
            productRepository.findFirstByCategoryNameOrderByPriceAscUpdatedAtDesc(categoryName).getOrElse {
                throw NotFoundException(ErrorCode.PRODUCT_NOT_FOUND)
            }
        val mostExpensiveProduct =
            productRepository.findFirstByCategoryNameOrderByPriceDescUpdatedAtDesc(categoryName).getOrElse {
                cheapestProduct
            }

        return CheapestAndMostExpensiveByCategory(
            categoryName = categoryName,
            cheapestProduct =
                ProductWithBrandAndPrice(
                    brandName = cheapestProduct.brand.name,
                    price = cheapestProduct.price.toInt(),
                ),
            mostExpensiveProduct =
                ProductWithBrandAndPrice(
                    brandName = mostExpensiveProduct.brand.name,
                    price = mostExpensiveProduct.price.toInt(),
                ),
        )
    }
}
