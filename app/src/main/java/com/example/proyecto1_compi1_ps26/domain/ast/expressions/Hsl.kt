package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Hsl(
    line: Int,
    column: Int,
    val h: Int,
    val s: Int,
    val l: Int
) : ColorExpression(line, column) {
}