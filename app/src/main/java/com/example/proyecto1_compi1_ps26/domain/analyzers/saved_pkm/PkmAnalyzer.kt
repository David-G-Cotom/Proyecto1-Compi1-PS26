package com.example.proyecto1_compi1_ps26.domain.analyzers.saved_pkm

import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport
import java.io.StringReader

class PkmAnalyzer {

    var errors: ArrayList<ErrorReport> = ArrayList()

    fun analyze(text: String): String {
        var result: String
        val lexer = LexerPKM(StringReader(text))
        val parser = ParserPKM(lexer)
        try {
            parser.parse()
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