package com.example.proyecto1_compi1_ps26.domain.translation.tags

class OpenQuestionTag(
    val width: Double,
    val height: Double,
    val label: String,
    val style: StyleTag?
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val attrs = "${width},${height},\"${label}\""
        if (style == null || style.isEmpty) {
            append("$i<open=$attrs/>")
        } else {
            appendLine("$i<open=$attrs>")
            appendLine(style.render(indent + 1))
            append("$i</open>")
        }
    }

}