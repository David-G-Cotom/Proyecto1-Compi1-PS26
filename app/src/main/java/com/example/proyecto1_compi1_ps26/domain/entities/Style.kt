package com.example.proyecto1_compi1_ps26.domain.entities

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.enums.BorderType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.FontFamily

class Style(
    val color: Expression?,
    val backgroundColor: Expression?,
    val fontFamily: FontFamily?,
    val textSize: Expression?,
    val border: Border?
) {
}
