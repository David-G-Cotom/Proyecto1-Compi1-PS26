package com.example.proyecto1_compi1_ps26.domain.ast

import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.PokemonFunction

sealed class OptionsSource {
}

data class LiteralOptions(val options: ArrayList<Expression>): OptionsSource()
data class PokemonOptions(val pokemonQuery: PokemonFunction): OptionsSource()