package com.mss.assignment.domain.brand

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
) {
    @Transactional
    fun createBrand(name: String): BrandDto {
        if (brandRepository.existsByName(name)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.CONFLICT_NAME)
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
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.CONFLICT_NAME)
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
        if (!brandRepository.existsById(id)) {
            throw NotFoundException(ErrorCode.BRAND_NOT_FOUND)
        }
        brandRepository.deleteById(id)
    }
}
