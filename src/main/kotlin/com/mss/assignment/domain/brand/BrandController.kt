package com.mss.assignment.domain.brand

import com.mss.assignment.domain.brand.request.CreateBrandRequest
import com.mss.assignment.domain.brand.request.UpdateBrandRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/brands")
class BrandController(
    private val brandService: BrandService
) {

    @PostMapping
    fun createBrand(@RequestBody request: CreateBrandRequest): ResponseEntity<Brand> {
        val createdBrand = brandService.createBrand(request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand)
    }

    @PutMapping("/{id}")
    fun updateBrand(
        @PathVariable id: Long,
        @RequestBody request: UpdateBrandRequest
    ): ResponseEntity<Brand> {
        val updatedBrand = brandService.updateBrand(id, request.name)
        return ResponseEntity.ok(updatedBrand)
    }

    @DeleteMapping("/{id}")
    fun deleteBrand(@PathVariable id: Long): ResponseEntity<Void> {
        brandService.deleteBrand(id)
        return ResponseEntity.noContent().build()
    }
}
