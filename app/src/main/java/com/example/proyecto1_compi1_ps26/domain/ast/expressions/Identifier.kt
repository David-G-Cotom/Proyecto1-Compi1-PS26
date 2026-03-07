package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Identifier(
    line: Int,
    column: Int,
    val name: String
): Expression(line, column) {
}