package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.Style

class Table(
    line: Int,
    column: Int,
    val attributes: Map<String, Expression>,
    val rows: List<List<Statement>>,
    val styles: Style?
) : Statement(line, column) {
}