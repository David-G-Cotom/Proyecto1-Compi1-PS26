package com.example.proyecto1_compi1_ps26.domain.translation.tags

class DropQuestionTag(
    val width: Double,
    val height: Double,
    val label: String,
    val options: List<String>,
    val correct: Int,
    val style: StyleTag?
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val opts = options.joinToString(",") { "\"${it}\"" }
        val attrs = "${width},${height},\"${label}\",{$opts},$correct"
        if (style == null || style.isEmpty) {
            append("$i<drop=$attrs/>")
        } else {
            appendLine("$i<drop=$attrs>")
            appendLine(style.render(indent + 1))
            append("$i</drop>")
        }
    }

}