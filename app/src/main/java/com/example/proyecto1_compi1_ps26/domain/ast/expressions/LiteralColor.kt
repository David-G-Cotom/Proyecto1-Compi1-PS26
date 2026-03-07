package com.example.proyecto1_compi1_ps26.domain.ast.expressions

import com.example.proyecto1_compi1_ps26.domain.entities.enums.ColorType

class LiteralColor(line: Int, column: Int, val value: ColorType):ColorExpression(line, column) {
}