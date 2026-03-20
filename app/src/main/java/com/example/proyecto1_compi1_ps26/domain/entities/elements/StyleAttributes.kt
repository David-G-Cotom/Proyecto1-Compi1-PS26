package com.example.proyecto1_compi1_ps26.domain.entities.elements

import com.example.proyecto1_compi1_ps26.domain.entities.enums.BorderType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.FontFamily

class StyleAttributes(
    val textColor: String? = null,
    val backgroundColor: String? = null,
    val fontFamily: FontFamily? = null,
    val textSize: Double? = null,
    val border: BorderAttributes? = null
) {
    companion object {
        val EMPTY = StyleAttributes()

        fun from(raw: Map<String, Any>): StyleAttributes {
            val border = (raw["border"] as? Map<*, *>)?.let {
                BorderAttributes(
                    (it["width"] as? Number)?.toInt() ?: 1,
                    it["type"] as BorderType,
                    it["color"] as String
                )
            }
            return StyleAttributes(
                raw["color"] as? String,
                raw["background color"] as? String,
                raw["font family"] as? FontFamily,
                raw["text size"] as? Double,
                border
            )
        }
    }
}