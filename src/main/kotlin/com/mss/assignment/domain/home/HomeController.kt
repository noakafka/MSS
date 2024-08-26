package com.mss.assignment.domain.home

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun home(): String {
        return "home" // "home.html"을 반환
    }

    @GetMapping("/admin/brands")
    fun brandManagement(): String {
        return "brand_management" // src/main/resources/templates/brand_management.html을 렌더링
    }

    @GetMapping("/admin/products")
    fun productManagement(): String {
        return "product_management" // src/main/resources/templates/product_management.html을 렌더링
    }
}
