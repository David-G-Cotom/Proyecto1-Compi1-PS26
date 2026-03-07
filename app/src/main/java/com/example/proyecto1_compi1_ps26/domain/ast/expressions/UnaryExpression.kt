package com.example.proyecto1_compi1_ps26.domain.ast.expressions

import com.example.proyecto1_compi1_ps26.domain.entities.enums.OperatorType

class UnaryExpression(
    line: Int,
    column: Int,
    val operator: OperatorType,
    val right: Expression
): Expression(line, column) {
}