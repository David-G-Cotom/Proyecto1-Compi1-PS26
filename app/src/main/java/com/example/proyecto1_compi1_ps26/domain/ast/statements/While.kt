package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression

class While(
    line: Int,
    column: Int,
    val condition: Expression,
    val body: ArrayList<ASTNode>
) : Statement(line, column) {
}