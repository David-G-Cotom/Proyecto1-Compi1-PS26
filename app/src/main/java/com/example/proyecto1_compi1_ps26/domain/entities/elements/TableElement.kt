package com.example.proyecto1_compi1_ps26.domain.entities.elements

class TableElement(
    width: Double?,
    height: Double?,
    val pointX: Double?,
    val pointY: Double?,
    val rows: List<List<FormElement>>,
    styles: StyleAttributes
) : FormElement(width, height, styles) {
}