package com.example.proyecto1_compi1_ps26.domain.translation.tags

class TableTag(
    val width: Double,
    val height: Double,
    val pointX: Double,
    val pointY: Double,
    val style: StyleTag?,
    val rows: List<List<PkmTag>>
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val i1 = ind(indent + 1)
        val i2 = ind(indent + 2)
        val i3 = ind(indent + 3)
        val i4 = ind(indent + 4)
        val attrs = "${width},${height},${pointX},${pointY}"

        appendLine("$i<table=$attrs>")
        style?.takeIf { !it.isEmpty }?.let { appendLine(it.render(indent + 1)) }
        appendLine("$i1<content>")
        rows.forEach { row ->
            appendLine("$i2<line>")
            row.forEach { cell ->
                appendLine("$i3<element>")
                appendLine(cell.render(indent + 4))
                appendLine("$i3</element>")
            }
            appendLine("$i2</line>")
        }
        appendLine("$i1</content>")
        append("$i</table>")
    }

}