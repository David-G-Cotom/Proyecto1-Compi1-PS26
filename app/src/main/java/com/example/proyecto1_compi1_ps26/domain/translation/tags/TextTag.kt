package com.example.proyecto1_compi1_ps26.domain.translation.tags

class TextTag(
    val width: Double,
    val height: Double,
    val content: String,
    val style: StyleTag?
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val attrs = "${width},${height},\"${content}\""
        if (style == null || style.isEmpty) {
            append("$i<text=$attrs/>")
        } else {
            appendLine("$i<text=$attrs>")
            appendLine(style.render(indent + 1))
            append("$i</text>")
        }
    }

}