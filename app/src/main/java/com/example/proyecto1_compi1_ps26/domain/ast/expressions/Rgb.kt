package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Rgb(
    line: Int,
    column: Int,
    val r: Expression,
    val g: Expression,
    val b: Expression
) : ColorExpression(line, column) {
}