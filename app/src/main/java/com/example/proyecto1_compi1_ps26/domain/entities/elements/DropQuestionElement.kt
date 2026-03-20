package com.example.proyecto1_compi1_ps26.domain.entities.elements

class DropQuestionElement(
    width: Double?,
    height: Double?,
    val label: String,
    val options: ArrayList<String>,
    val correct: Int?,
    styles: StyleAttributes
) : FormElement(width, height, styles) {
}