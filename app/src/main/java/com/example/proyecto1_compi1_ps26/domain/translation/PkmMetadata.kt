package com.example.proyecto1_compi1_ps26.domain.translation

import com.example.proyecto1_compi1_ps26.domain.translation.tags.PkmTag

class PkmMetadata(
    val author: String,
    val date: String,
    val time: String,
    val description: String,
    val totalSections: Int,
    val totalOpen: Int,
    val totalDrop: Int,
    val totalSelect: Int,
    val totalMultiple: Int
) : PkmTag() {

    private val totalQuestions =
        this.totalOpen + this.totalDrop + this.totalSelect + this.totalMultiple

    override fun render(indent: Int) = buildString {
        appendLine("###")
        appendLine("    Author: $author")
        appendLine("    Fecha: $date")
        appendLine("    Hora: $time")
        appendLine("    Description: $description")
        appendLine("    Total de Secciones: $totalSections")
        appendLine("    Total de Preguntas: $totalQuestions")
        appendLine("        Abiertas: $totalOpen")
        appendLine("        Desplegables: $totalDrop")
        appendLine("        Selección: $totalSelect")
        appendLine("        Múltiples: $totalMultiple")
        append("###")
    }

}