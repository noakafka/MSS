package com.mss.assignment.domain.brand

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
    fun getBrandById(id: Long): Brand {
        return brandRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
    }

    @Transactional
    fun createBrand(name: String): Brand {
        if (brandRepository.existsByName(name)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.CONFLICT_NAME)
        }
        val brand = Brand(name = name)
        return brandRepository.save(brand)
    }

    @Transactional
    fun updateBrand(
        id: Long,
        name: String,
    ): Brand {
        val brand = getBrandById(id)
        if (brandRepository.existsByName(name)) {
            throw GlobalHttpException(HttpStatus.CONFLICT, ErrorCode.CONFLICT_NAME)
        }
        brand.updateName(name)
        return brandRepository.save(brand)
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
