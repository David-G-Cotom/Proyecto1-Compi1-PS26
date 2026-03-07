package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class AssignFor(
    line: Int,
    column: Int,
    val id: String,
    val value: Expression
): Expression(line, column) {
}