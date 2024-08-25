package com.mss.assignment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
class AssignmentApplication

fun main(args: Array<String>) {
    runApplication<AssignmentApplication>(*args)
}
