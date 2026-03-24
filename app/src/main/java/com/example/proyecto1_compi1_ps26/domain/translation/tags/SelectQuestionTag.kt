package com.example.proyecto1_compi1_ps26.domain.translation.tags

class SelectQuestionTag(
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
            append("$i<select=$attrs/>")
        } else {
            appendLine("$i<select=$attrs>")
            appendLine(style.render(indent + 1))
            append("$i</select>")
        }
    }

}