package com.example.proyecto1_compi1_ps26.domain.ast.expressions

import com.example.proyecto1_compi1_ps26.domain.entities.enums.OperatorType

class BinaryExpression(
    line: Int,
    column: Int,
    val left: Expression,
    val right: Expression,
    val operator: OperatorType
): Expression(line, column) {
}