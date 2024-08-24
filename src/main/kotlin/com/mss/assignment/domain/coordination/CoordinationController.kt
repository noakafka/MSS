package com.mss.assignment.domain.coordination

import com.mss.assignment.domain.coordination.response.CheapestCoordinationByBrand
import com.mss.assignment.dto.CheapestEachCategory
import com.mss.assignment.dto.CheapestAndMostExpensiveByCategory
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
    fun getLowestPriceAndTotalByCategory(): ResponseEntity<CheapestEachCategory> {
        val response = coordinationService.findCheapestEachCategory()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/price-summary-by-category")
    fun getPriceSummaryByCategory(
        @RequestParam("category") category: String
    ): ResponseEntity<CheapestAndMostExpensiveByCategory> {
        val response = coordinationService.getCheapestAndMostExpensiveByCategory(category)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/cheapest-coordination-by-brand")
    fun getCheapestOutfitByBrand(): ResponseEntity<CheapestCoordinationByBrand> {
        val response = coordinationService.findCheapestCoordinationByBrand()
        return ResponseEntity.ok(response)
    }
}