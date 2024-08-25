package com.mss.assignment.domain.brand

import com.mss.assignment.exception.ErrorCode
import com.mss.assignment.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository
) {
    fun getBrandById(id: Long): Brand {
        return brandRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
    }

    @Transactional
    fun createBrand(name: String): Brand {
        val brand = Brand(name = name)
        return brandRepository.save(brand)
    }

    @Transactional
    fun updateBrand(id: Long, name: String): Brand {
        val brand = getBrandById(id)
        brand.updateName(name)
        return brandRepository.save(brand)
    }

    @Transactional
    @CacheEvict(cacheNames = ["cheapestProductEachCategory", "cheapestAndMostExpensiveByCategory", "cheapestCoordinationByBrand"], allEntries = true)
    fun deleteBrand(id: Long) {
        if (!brandRepository.existsById(id)) { NotFoundException(ErrorCode.BRAND_NOT_FOUND) }
        brandRepository.deleteById(id)
    }
}