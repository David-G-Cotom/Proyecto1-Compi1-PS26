package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.Styles
import com.example.proyecto1_compi1_ps26.domain.entities.enums.OrientationType

class Section(
    line: Int,
    column: Int,
    val width: Expression,
    val height: Expression,
    val pointX: Expression,
    val pointY: Expression,
    val orientation: OrientationType?,
    val elements: ArrayList<ASTNode>?,
    val styles: Styles?
) : Statement(line, column) {
}