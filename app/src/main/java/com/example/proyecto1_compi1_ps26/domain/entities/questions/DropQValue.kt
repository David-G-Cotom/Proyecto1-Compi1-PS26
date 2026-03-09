package com.example.proyecto1_compi1_ps26.domain.entities.questions

import com.example.proyecto1_compi1_ps26.domain.entities.SpecialValue

class DropQValue(
    val width: Double?,
    val height: Double?,
    val label: String,
    val options: ArrayList<String>,
    val correct: Int?,
    val styles: Map<String, Any>?,
    wildcard: Int = 0
) : SpecialValue(wildcard) {
}