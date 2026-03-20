package com.example.proyecto1_compi1_ps26.domain.entities.elements

import com.example.proyecto1_compi1_ps26.domain.entities.enums.OrientationType

class SectionElement(
    width: Double?,
    height: Double?,
    val pointX: Double?,
    val pointY: Double?,
    val orientation: OrientationType,
    val children: List<FormElement>,
    styles: StyleAttributes
) : FormElement(width, height, styles) {
}