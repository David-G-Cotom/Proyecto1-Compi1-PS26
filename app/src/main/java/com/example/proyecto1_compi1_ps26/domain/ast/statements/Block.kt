package com.example.proyecto1_compi1_ps26.domain.ast.statements

class Block(
    line: Int,
    column: Int,
    val statements: List<Statement>
) : Statement(line, column) {
}