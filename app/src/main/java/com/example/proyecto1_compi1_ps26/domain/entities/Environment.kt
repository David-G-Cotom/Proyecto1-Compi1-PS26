package com.example.proyecto1_compi1_ps26.domain.entities

import com.example.proyecto1_compi1_ps26.domain.entities.enums.VariableType
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.SpecialValue

class Environment(val parent: Environment? = null) {

    private val variables = HashMap<String, SymbolValue>()

    // tipo id valor
    // tipo id valorDefault
    fun define(name: String, type: VariableType, value: Any, line: Int, column: Int) {
        if (this.variables.containsKey(name)) {
            throw Exception("Error: Variable $name ya definida en este ambito")
        }
        this.validateTypeAndValue(name, type, value)
        this.variables[name] = SymbolValue(name, type, value, line, column)
    }

    private fun validateTypeAndValue(name: String, type: VariableType, value: Any) {
        val typeCorrect = when (type) {
            VariableType.NUMBER -> value is Int || value is Double
            VariableType.STRING -> value is String
            VariableType.SPECIAL -> value is SpecialValue
        }
        if (!typeCorrect) {
            throw Exception("Error: Tipo de dato no compatible con variable $name")
        }
    }

    fun getSymbol(name: String): SymbolValue {
        if (this.variables.containsKey(name)) {
            return this.variables[name]!!
        }
        if (this.parent != null) {
            return this.parent.getSymbol(name)
        }
        throw Exception("Error: Variable $name no definida")
    }

    // id valor
    fun assign(name: String, value: Any) {
        if (this.variables.containsKey(name)) {
            val symbol = this.variables[name]!!
            this.validateTypeAndValue(name, symbol.type, value)
            symbol.value = value
            return
        }
        if (this.parent != null) {
            this.parent.assign(name, value)
            return
        }
        throw Exception("Error: Variable $name no definida")
    }

    fun isDeclared(name: String): Boolean {
        if (this.variables.containsKey(name)) {
            return true
        }
        if (this.parent != null) {
            return this.parent.isDeclared(name)
        }
        return false
    }

    fun isDeclaredLocally(name: String): Boolean = this.variables.containsKey(name)

    fun createChild(): Environment = Environment(this)

}