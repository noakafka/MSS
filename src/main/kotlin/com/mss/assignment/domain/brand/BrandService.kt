package com.mss.assignment.domain.brand

import com.mss.assignment.domain.product.ProductRepository
import com.mss.assignment.dto.BrandDto
import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.GlobalHttpException
import com.mss.assignment.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun createBrand(name: String): BrandDto {
        if (brandRepository.existsByName(name)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.NAME_CONFLICT)
        }
        val brand = Brand(name = name)
        return BrandDto.fromEntity(brandRepository.save(brand))
    }

    @Transactional
    fun updateBrand(
        id: Long,
        name: String,
    ): BrandDto {
        val brand = getBrandById(id)
        if (brandRepository.existsByName(name)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.NAME_CONFLICT)
        }
        brand.updateName(name)
        return BrandDto.fromEntity(brandRepository.save(brand))
    }

    private fun getBrandById(id: Long): Brand {
        return brandRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
    }

    @Transactional
    @CacheEvict(
        cacheNames = ["cheapestProductEachCategory", "cheapestAndMostExpensiveByCategory", "cheapestCoordinationByBrand"],
        allEntries = true,
    )
    fun deleteBrand(id: Long) {
        // 브랜드 존재 여부 확인
        if (!brandRepository.existsById(id)) {
            throw NotFoundException(ErrorCode.BRAND_NOT_FOUND)
        }
        // 해당 브랜드가 상품을 가지고 있는지 확인
        if (productRepository.existsByBrandId(id)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.BRAND_HAS_PRODUCTS)
        }

        brandRepository.deleteById(id)
    }
}
