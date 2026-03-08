package com.example.proyecto1_compi1_ps26.domain.entities

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.ColorExpression
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.enums.BorderType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.FontFamily

sealed class StyleValue {
}

data class ColorStyleValue (val color: ColorExpression): StyleValue()
data class FontStyleValue (val font: FontFamily): StyleValue()
data class TextSizeStyleValue (val size: Expression): StyleValue()
data class BorderStyleValue (
    val width: Expression,
    val type: BorderType,
    val color: ColorExpression
): StyleValue()

data class Styles(val entries: Map<String, StyleValue>)