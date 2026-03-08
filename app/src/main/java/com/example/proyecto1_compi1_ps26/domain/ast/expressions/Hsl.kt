package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Hsl(
    line: Int,
    column: Int,
    val h: Expression,
    val s: Expression,
    val l: Expression
) : ColorExpression(line, column) {
}