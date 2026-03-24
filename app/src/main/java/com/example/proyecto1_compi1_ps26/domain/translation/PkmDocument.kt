package com.example.proyecto1_compi1_ps26.domain.translation

import com.example.proyecto1_compi1_ps26.domain.translation.tags.PkmTag

class PkmDocument(val metadata: PkmMetadata, val elements: List<PkmTag>) {

    fun generateContent(): String = buildString {
        appendLine(metadata.render())
        appendLine()
        elements.forEachIndexed { i, tag ->
            append(tag.render(0))
            if (i < elements.lastIndex) appendLine()
            appendLine()
        }
    }

}