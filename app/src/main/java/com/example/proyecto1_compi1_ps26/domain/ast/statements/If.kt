package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression

class If(
    line: Int,
    column: Int,
    val condition: Expression,
    val thenStatement: Statement,
    val elseStatement: Statement?
) : Statement(line, column) {
}