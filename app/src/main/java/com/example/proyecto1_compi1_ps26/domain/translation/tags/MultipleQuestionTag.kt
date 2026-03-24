package com.example.proyecto1_compi1_ps26.domain.translation.tags

class MultipleQuestionTag(
    val width: Double,
    val height: Double,
    val label: String,
    val options: List<String>,
    val correct: List<Int>,
    val style: StyleTag?
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val opts = options.joinToString(",") { "\"${it}\"" }
        val corr = correct.joinToString(",")
        val attrs = "${width},${height},\"${label}\",{$opts},{$corr}"
        if (style == null || style.isEmpty) {
            append("$i<multiple=$attrs/>")
        } else {
            appendLine("$i<multiple=$attrs>")
            appendLine(style.render(indent + 1))
            append("$i</multiple>")
        }
    }

}