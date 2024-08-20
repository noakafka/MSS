package com.mss.assignment.domain.coordination

import com.mss.assignment.dto.LowestPriceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coordinations")
class CoordinationController(
    private val coordinationService: CoordinationService
) {
    @GetMapping("/lowest-price-by-category")
    fun getLowestPriceAndTotalByCategory(): ResponseEntity<LowestPriceResponse> {
        val response = coordinationService.findLowestPriceProductsByCategory()
        return ResponseEntity.ok(response)
    }
}