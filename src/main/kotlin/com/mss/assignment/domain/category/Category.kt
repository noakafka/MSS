package com.mss.assignment.domain.category

import com.mss.assignment.domain.BaseEntity
import jakarta.persistence.Entity

@Entity
data class Category(
    val name: String
) : BaseEntity()