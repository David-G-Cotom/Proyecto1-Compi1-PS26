package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression

class For(
    line: Int,
    column: Int,
    val init: Statement,
    val condition: Expression,
    val update: Expression,
    val body: Statement
) : Statement(line, column) {
}