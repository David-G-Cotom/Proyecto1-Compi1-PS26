package com.example.proyecto1_compi1_ps26.domain.translation.tags

import com.example.proyecto1_compi1_ps26.domain.entities.elements.StyleAttributes

class StyleTag(val styles: StyleAttributes) : PkmTag() {

    val isEmpty: Boolean = this.styles.textColor == null &&
            this.styles.backgroundColor == null &&
            this.styles.fontFamily == null &&
            this.styles.textSize == null &&
            this.styles.border == null

    override fun render(indent: Int) = buildString {
        val i = ind(indent)
        val i1 = ind(indent + 1)
        appendLine("$i<style>")
        styles.textColor?.let { appendLine("$i1<color=${it}/>") }
        styles.backgroundColor?.let { appendLine("$i1<background color=${it}/>") }
        styles.fontFamily?.let { appendLine("$i1<font family=${it.name}/>") }
        styles.textSize?.let { appendLine("$i1<text size=${it.toInt()}/>") }
        styles.border?.let { appendLine("$i1${it.renderBorder()}") }
        append("$i</style>")
    }

}