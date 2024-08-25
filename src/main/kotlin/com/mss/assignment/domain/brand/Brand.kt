package com.mss.assignment.domain.brand

import com.mss.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Brand(
    name: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var name: String = name
        private set

    fun updateName(newName: String) {
        this.name = newName
    }
}
