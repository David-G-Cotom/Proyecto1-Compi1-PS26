package com.example.proyecto1_compi1_ps26.domain.entities

import com.example.proyecto1_compi1_ps26.domain.entities.enums.VariableType

class SymbolValue(
    val name: String,
    val type: VariableType,
    var value: Any?,
    val line: Int,
    val column: Int
) {
}