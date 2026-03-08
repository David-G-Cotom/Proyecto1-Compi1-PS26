package com.example.proyecto1_compi1_ps26.domain.ast.expressions

import com.example.proyecto1_compi1_ps26.domain.entities.enums.ValueType

class Literal(
    line: Int,
    column: Int,
    val value: Any?,
    val type: ValueType
) : Expression(line, column) {
}