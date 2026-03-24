package com.example.proyecto1_compi1_ps26.domain.analyzers.form_creation

import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.Program
import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport
import com.example.proyecto1_compi1_ps26.domain.entities.Interpreter
import com.example.proyecto1_compi1_ps26.domain.entities.elements.FormElement
import java.io.StringReader

class FormAnalyzer {

    var errors: ArrayList<ErrorReport> = ArrayList()
    lateinit var interpreter: Interpreter

    fun analyze(text: String): String {
        var result: String
        val lexer = LexerForm(StringReader(text))
        val parser = ParserForm(lexer)
        try {
            val ast = Program(0, 0, parser.parse().value as ArrayList<ASTNode>)
            this.interpreter = Interpreter()
            this.interpreter.run(ast)
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
            result = "Error -> " + e.message
        }
        return result
    }

}