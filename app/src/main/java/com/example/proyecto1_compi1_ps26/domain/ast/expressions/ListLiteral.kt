package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class ListLiteral(
    line: Int,
    column: Int,
    val elements: List<Expression>
) : Expression(line, column) {
}