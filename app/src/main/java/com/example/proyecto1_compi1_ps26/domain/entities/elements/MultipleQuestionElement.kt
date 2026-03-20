package com.example.proyecto1_compi1_ps26.domain.entities.elements

class MultipleQuestionElement(
    width: Double?,
    height: Double?,
    val label: String?,
    val options: ArrayList<String>,
    val correct: List<Int>?,
    styles: StyleAttributes
) : FormElement(width, height, styles) {
}