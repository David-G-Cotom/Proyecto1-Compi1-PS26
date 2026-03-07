package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.Style

class MultipleQuestion(
    line: Int,
    column: Int,
    val attributes: Map<String, Expression>,
    val styles: Style?
) : Statement(line, column) {
}