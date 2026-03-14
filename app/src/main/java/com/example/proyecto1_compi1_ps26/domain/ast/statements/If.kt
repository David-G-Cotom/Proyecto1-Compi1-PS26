package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression

class If(
    line: Int,
    column: Int,
    val condition: Expression,
    val thenStatement: ArrayList<ASTNode>,
    val elseIfStatement: ArrayList<ElseIf>?,
    val elseStatement: ArrayList<ASTNode>?
) : Statement(line, column) {
}