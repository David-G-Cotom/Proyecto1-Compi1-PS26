package com.example.proyecto1_compi1_ps26.domain.entities.questions

import com.example.proyecto1_compi1_ps26.domain.entities.SpecialValue

class MultipleQValue(
    val width: Double?,
    val height: Double?,
    val options: ArrayList<String>,
    val correct: ArrayList<Int>?,
    val styles: Map<String, Any>?,
    wildcard: Int = 0
) : SpecialValue(wildcard) {
}