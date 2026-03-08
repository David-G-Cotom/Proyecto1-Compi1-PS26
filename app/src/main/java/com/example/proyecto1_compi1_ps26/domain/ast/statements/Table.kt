package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.Styles

class Table(
    line: Int,
    column: Int,
    val width: Expression,
    val height: Expression,
    val pointX: Expression,
    val pointY: Expression,
    val elements: ArrayList<Statement>,
    val styles: Styles?
) : Statement(line, column) {
}