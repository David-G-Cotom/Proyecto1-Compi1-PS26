package com.example.proyecto1_compi1_ps26.domain.translation

import android.annotation.SuppressLint
import com.example.proyecto1_compi1_ps26.domain.entities.Interpreter
import com.example.proyecto1_compi1_ps26.domain.entities.elements.DropQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.FormElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.MultipleQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.OpenQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SectionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SelectQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.StyleAttributes
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TableElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TextElement
import com.example.proyecto1_compi1_ps26.domain.translation.tags.DropQuestionTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.MultipleQuestionTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.OpenQuestionTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.PkmTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.SectionTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.SelectQuestionTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.StyleTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.TableTag
import com.example.proyecto1_compi1_ps26.domain.translation.tags.TextTag
import java.text.SimpleDateFormat
import java.util.Date

class PkmTranslator(private val author: String = "", private val description: String = "") {

    @SuppressLint("SimpleDateFormat")
    fun translate(interpreter: Interpreter): PkmDocument {
        val now = Date()
        val metadata = PkmMetadata(
            author,
            SimpleDateFormat("dd/MM/yyyy").format(now),
            SimpleDateFormat("HH:mm").format(now),
            description,
            interpreter.sectionCount,
            interpreter.openCount,
            interpreter.dropCount,
            interpreter.selectCount,
            interpreter.multipleCount
        )

        val tags = interpreter.formOutput.map { toTag(it) }

        return PkmDocument(metadata, tags)
    }

    private fun toTag(elem: FormElement): PkmTag = when (elem) {
        is SectionElement -> toSection(elem)
        is TableElement -> toTable(elem)
        is TextElement -> toText(elem)
        is OpenQuestionElement -> toOpen(elem)
        is DropQuestionElement -> toDrop(elem)
        is SelectQuestionElement -> toSelect(elem)
        is MultipleQuestionElement -> toMultiple(elem)
        else -> throw Exception("Error: Tipo de Elemento no soportado")
    }

    private fun toSection(elem: SectionElement) = SectionTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.pointX ?: 0.0,
        elem.pointY ?: 0.0,
        elem.orientation,
        styleOrNull(elem.styles),
        elem.children.map { toTag(it) }
    )

    private fun toTable(elem: TableElement) = TableTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.pointX ?: 0.0,
        elem.pointY ?: 0.0,
        styleOrNull(elem.styles),
        elem.rows.map { row -> row.map { toTag(it) } }
    )

    private fun toText(elem: TextElement) = TextTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.content,
        styleOrNull(elem.styles)
    )

    private fun toOpen(elem: OpenQuestionElement) = OpenQuestionTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.label,
        styleOrNull(elem.styles)
    )

    private fun toDrop(elem: DropQuestionElement) = DropQuestionTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.label,
        elem.options,
        elem.correct ?: -1,
        styleOrNull(elem.styles)
    )

    private fun toSelect(elem: SelectQuestionElement) = SelectQuestionTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.label ?: "",
        elem.options,
        elem.correct ?: 0,
        styleOrNull(elem.styles)
    )

    private fun toMultiple(elem: MultipleQuestionElement) = MultipleQuestionTag(
        elem.width  ?: 0.0,
        elem.height ?: 0.0,
        elem.label ?: "",
        elem.options,
        elem.correct ?: emptyList(),
        styleOrNull(elem.styles)
    )

    private fun styleOrNull(styles: StyleAttributes): StyleTag? {
        val tag = StyleTag(styles)
        return if (tag.isEmpty) null else tag
    }

}