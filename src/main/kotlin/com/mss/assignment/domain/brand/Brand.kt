package com.mss.assignment.domain.brand

import com.mss.assignment.domain.BaseEntity
import jakarta.persistence.Entity

@Entity
data class Brand(
    val name: String
) : BaseEntity()