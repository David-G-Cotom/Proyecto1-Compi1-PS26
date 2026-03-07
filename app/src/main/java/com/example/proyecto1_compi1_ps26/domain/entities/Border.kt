package com.example.proyecto1_compi1_ps26.domain.entities

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.enums.BorderType

class Border(
    val type: BorderType,
    val width: Expression,
    val color: Expression
) {
}