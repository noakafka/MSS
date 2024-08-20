package com.mss.assignment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class AssignmentApplication

fun main(args: Array<String>) {
	runApplication<AssignmentApplication>(*args)
}
