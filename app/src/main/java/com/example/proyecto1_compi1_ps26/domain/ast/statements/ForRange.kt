package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression

class ForRange(
    line: Int,
    column: Int,
    val id: String,
    val start: Expression,
    val end: Expression,
    val body: ArrayList<ASTNode>
) : Statement(line, column) {
}