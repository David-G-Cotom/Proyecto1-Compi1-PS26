package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.enums.VariableType

class VarDecl(
    line: Int,
    column: Int,
    val id: String,
    val type: VariableType?,
    val value: Expression?
) : Statement(line, column) {
}