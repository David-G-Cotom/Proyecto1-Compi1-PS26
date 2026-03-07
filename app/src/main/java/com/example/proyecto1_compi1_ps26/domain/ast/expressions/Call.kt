package com.example.proyecto1_compi1_ps26.domain.ast.expressions

class Call(
    line: Int,
    column: Int,
    val callee: Expression,
    val arguments: List<Expression>
): Expression(line, column) {
}