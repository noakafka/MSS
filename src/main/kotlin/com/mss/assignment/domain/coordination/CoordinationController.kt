package com.mss.assignment.domain.coordination

import com.mss.assignment.dto.LowestPriceResponse
import com.mss.assignment.dto.PriceSummary
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/price-summary-by-category")
    fun getPriceSummaryByCategory(
        @RequestParam("category") category: String
    ): ResponseEntity<PriceSummary> {
        val response = coordinationService.getPriceSummaryForCategory(category)
        return ResponseEntity.ok(response)
    }
}