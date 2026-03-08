package com.example.proyecto1_compi1_ps26.domain.ast.statements

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.entities.OptionsSource
import com.example.proyecto1_compi1_ps26.domain.entities.Styles

class SelectQuestion(
    line: Int,
    column: Int,
    val width  : Expression?,
    val height : Expression?,
    val label  : Expression?,
    val options: OptionsSource,
    val correct: Expression?,
    val styles: Styles?
) : Expression(line, column) {
}