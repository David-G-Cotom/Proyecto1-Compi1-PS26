package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Call(
    line: Int,
    column: Int,
    val varName: String,
    val arguments: ArrayList<Expression>
): Expression(line, column) {
}