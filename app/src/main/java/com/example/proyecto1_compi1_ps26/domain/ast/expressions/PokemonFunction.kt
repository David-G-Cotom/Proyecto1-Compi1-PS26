package com.example.proyecto1_compi1_ps26.domain.ast.expressions

import com.example.proyecto1_compi1_ps26.domain.entities.enums.PokemonQueryType

class PokemonFunction(
    line: Int,
    column: Int,
    val type: PokemonQueryType,
    val from: Expression,
    val to: Expression
) : Expression(line, column) {
}