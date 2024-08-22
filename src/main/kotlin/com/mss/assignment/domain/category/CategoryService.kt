package com.mss.assignment.domain.category

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    @Cacheable("categoryIds")
    fun findAllCategoryIds(): List<Long> {
        return categoryRepository.findAll().map { it.id }
    }
}