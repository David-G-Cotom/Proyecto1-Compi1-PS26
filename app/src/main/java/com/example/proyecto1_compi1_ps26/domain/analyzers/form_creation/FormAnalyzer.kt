package com.example.proyecto1_compi1_ps26.domain.analyzers.form_creation

import com.example.proyecto1_compi1_ps26.domain.ast.Program
import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport
import com.example.proyecto1_compi1_ps26.domain.entities.Interpreter
import com.example.proyecto1_compi1_ps26.domain.entities.elements.FormElement
import java.io.StringReader

class FormAnalyzer {

    var errors: ArrayList<ErrorReport> = ArrayList()
    var elements = mutableListOf<FormElement>()

    fun analyze(text: String): String {
        var result: String
        val lexer = LexerForm(StringReader(text))
        val parser = ParserForm(lexer)
        try {
            val ast = parser.parse().value as Program
            val interpreter = Interpreter()
            interpreter.run(ast)
            this.elements = interpreter.formOutput
            for (error in lexer.lexicalErrors) {
                this.errors.add(error)
            }
            for (error in parser.syntaxErrors) {
                this.errors.add(error)
            }
            result = "Analisis Finalizado"
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
            e.cause
            e.stackTrace
            result = "Error Inesperado -> " + e.message
        }
        return result
    }

}