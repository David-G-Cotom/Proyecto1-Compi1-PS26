package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Rgb(
    line: Int,
    column: Int,
    val r: Int,
    val g: Int,
    val b: Int
) : ColorExpression(line, column) {
}