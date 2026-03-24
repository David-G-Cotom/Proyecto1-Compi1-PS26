package com.example.proyecto1_compi1_ps26.domain.translation.tags

import com.example.proyecto1_compi1_ps26.domain.entities.enums.OrientationType

class SectionTag(
    val width: Double,
    val height: Double,
    val pointX: Double,
    val pointY: Double,
    val orientation: OrientationType,
    val style: StyleTag?,
    val children: List<PkmTag>
) : PkmTag() {

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val i1 = ind(indent + 1)
        val i2 = ind(indent + 2)
        val attrs =
            "${width},${height},${pointX},${pointY},${orientation.name}"

        appendLine("$i<section=$attrs>")
        style?.takeIf { !it.isEmpty }?.let { appendLine(it.render(indent + 1)) }
        appendLine("$i1<content>")
        children.forEach { child ->
            appendLine(child.render(indent + 2))
        }
        appendLine("$i1</content>")
        append("$i</section>")
    }

}