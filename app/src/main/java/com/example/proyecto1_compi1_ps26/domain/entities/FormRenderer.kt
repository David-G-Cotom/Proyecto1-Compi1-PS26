package com.example.proyecto1_compi1_ps26.domain.entities

//noinspection SuspiciousImport
import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.proyecto1_compi1_ps26.domain.entities.elements.DropQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.FormElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.MultipleQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.OpenQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SectionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SelectQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.StyleAttributes
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TableElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TextElement
import com.example.proyecto1_compi1_ps26.domain.entities.enums.BorderType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.OrientationType
import androidx.core.graphics.toColorInt
import com.example.proyecto1_compi1_ps26.domain.entities.enums.FontFamily

class FormRenderer(private val context: Context) {

    private val defaultTextColor = Color.BLACK
    private val defaultBgColor = Color.WHITE
    private val defaultTextSizeSp = 14f
    private val elementMarginDp = 8

    fun render(elements: List<FormElement>, container: LinearLayout) {
        container.removeAllViews()
        elements.forEach { element ->
            val view = buildView(element)
            container.addView(view)
        }
    }

    private fun buildView(element: FormElement): View = when (element) {
        is SectionElement -> buildSection(element)
        is TableElement -> buildTable(element)
        is TextElement -> buildText(element)
        is OpenQuestionElement -> buildOpenQuestion(element)
        is DropQuestionElement -> buildDropQuestion(element)
        is SelectQuestionElement -> buildSelectQuestion(element)
        is MultipleQuestionElement -> buildMultipleQuestion(element)
        else -> {}
    } as View

    private fun buildSection(elem: SectionElement): LinearLayout {
        val layout = LinearLayout(this.context).apply {
            orientation = when (elem.orientation) {
                OrientationType.VERTICAL -> LinearLayout.VERTICAL
                OrientationType.HORIZONTAL -> LinearLayout.HORIZONTAL
            }
            layoutParams = buildLayoutParams(elem.width, elem.height)
            applyStyles(this, elem.styles)
        }
        elem.pointX?.let { layout.tag = "pointX:${it.toInt()}" }

        elem.children.forEach { child ->
            layout.addView(buildView(child))
        }
        return layout
    }

    private fun buildLayoutParams(width: Double?, height: Double?): LinearLayout.LayoutParams {
        val w = width?.let { dpToPx(it.toInt()) } ?: MATCH_PARENT
        val h = height?.let { dpToPx(it.toInt()) } ?: WRAP_CONTENT
        return LinearLayout.LayoutParams(w, h).also {
            it.setMargins(0, dpToPx(elementMarginDp), 0, 0)
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * this.context.resources.displayMetrics.density).toInt()

    private fun applyStyles(view: View, styles: StyleAttributes) {
        val bgColor = styles.backgroundColor?.toColorInt() ?: this.defaultBgColor
        view.setBackgroundColor(bgColor)

        styles.border?.let { border ->
            val drawable = GradientDrawable().apply {
                setColor(bgColor)
                setStroke(
                    dpToPx(border.width),
                    border.color.toColorInt()
                )
                if (border.type == BorderType.DOTTED) {
                    setStroke(
                        dpToPx(border.width), border.color.toColorInt(),
                        dpToPx(4).toFloat(), dpToPx(4).toFloat()
                    )
                }
                if (border.type == BorderType.DOUBLE) {
                    setStroke(dpToPx(border.width * 2), border.color.toColorInt())
                }
            }
            view.background = drawable
        }
    }

    private fun buildTable(elem: TableElement): TableLayout {
        val table = TableLayout(this.context).apply {
            layoutParams = buildLayoutParams(elem.width, elem.height)
            isStretchAllColumns = true
            applyStyles(this, elem.styles)
        }

        elem.rows.forEach { rowElements ->
            val row = TableRow(this.context).apply {
                layoutParams = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }
            rowElements.forEach { cellElement ->
                val cellView = buildView(cellElement)
                val cellParams = TableRow.LayoutParams(0, WRAP_CONTENT, 1f)
                cellParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
                cellView.layoutParams = cellParams
                row.addView(cellView)
            }
            table.addView(row)
        }
        return table
    }

    private fun buildText(elem: TextElement): TextView {
        return TextView(this.context).apply {
            text = elem.content
            layoutParams = buildLayoutParams(elem.width, elem.height)
            applyTextStyles(this, elem.styles)
            applyStyles(this, elem.styles)
        }
    }

    private fun applyTextStyles(view: TextView, styles: StyleAttributes) {
        view.setTextColor(styles.textColor?.toColorInt() ?: this.defaultTextColor)

        styles.textSize?.let { size ->
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        } ?: run {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.defaultTextSizeSp)
        }

        styles.fontFamily?.let { family ->
            view.typeface = when (family) {
                FontFamily.MONO -> Typeface.MONOSPACE
                FontFamily.SANS_SERIF -> Typeface.SANS_SERIF
                FontFamily.CURSIVE -> Typeface.create("cursive", Typeface.NORMAL)
            }
        }
    }

    private fun buildOpenQuestion(elem: OpenQuestionElement): LinearLayout {
        return questionContainer(elem.width, elem.height, elem.styles).apply {
            addView(buildLabel(elem.label, elem.styles))
            addView(EditText(this.context).apply {
                hint = elem.label
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    .also { it.topMargin = dpToPx(4) }
                applyTextStyles(this, elem.styles)
            })
        }
    }

    private fun questionContainer(
        width: Double?,
        height: Double?,
        styles: StyleAttributes
    ): LinearLayout = LinearLayout(this.context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = buildLayoutParams(width, height)
        setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        applyStyles(this, styles)
    }

    private fun buildLabel(text: String, styles: StyleAttributes): TextView =
        TextView(this.context).apply {
            this.text = text
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            applyTextStyles(this, styles)
        }

    private fun buildDropQuestion(elem: DropQuestionElement): LinearLayout {
        return questionContainer(elem.width, elem.height, elem.styles).apply {
            addView(buildLabel(elem.label, elem.styles))

            val spinner = Spinner(this.context).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    .also { it.topMargin = dpToPx(4) }
                adapter = ArrayAdapter(
                    this.context,
                    R.layout.simple_spinner_item,
                    elem.options
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            }
            addView(spinner)
        }
    }

    private fun buildSelectQuestion(elem: SelectQuestionElement): LinearLayout {
        return questionContainer(elem.width, elem.height, elem.styles).apply {
            elem.label?.let { addView(buildLabel(it, elem.styles)) }

            val radioGroup = RadioGroup(this.context).apply {
                orientation = RadioGroup.VERTICAL
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }
            elem.options.forEachIndexed { index, option ->
                val radio = RadioButton(this.context).apply {
                    text = option
                    id = View.generateViewId()
                    applyTextStyles(this, elem.styles)
                    isChecked = (index + 1) == elem.correct
                }
                radioGroup.addView(radio)
            }
            addView(radioGroup)
        }
    }

    private fun buildMultipleQuestion(elem: MultipleQuestionElement): LinearLayout {
        return questionContainer(elem.width, elem.height, elem.styles).apply {
            elem.label?.let { addView(buildLabel(it, elem.styles)) }

            elem.options.forEachIndexed { index, option ->
                val checkBox = CheckBox(this.context).apply {
                    text = option
                    isChecked = elem.correct?.contains(index + 1) == true
                    applyTextStyles(this, elem.styles)
                }
                addView(checkBox)
            }
        }
    }

}