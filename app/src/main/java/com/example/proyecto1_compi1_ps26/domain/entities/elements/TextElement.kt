package com.example.proyecto1_compi1_ps26.domain.entities.elements

class TextElement(
    width: Double?,
    height: Double?,
    val content: String,
    styles: StyleAttributes
) : FormElement(width, height, styles) {
}