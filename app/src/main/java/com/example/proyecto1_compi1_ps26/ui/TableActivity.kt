package com.example.proyecto1_compi1_ps26.ui

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1_compi1_ps26.R
import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport
import com.example.proyecto1_compi1_ps26.ui.ContentActivity.Companion.ERROR_REPORT

class TableActivity : AppCompatActivity() {

    private lateinit var tbTemplate: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_table)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.tbTemplate = findViewById(R.id.tbTemplate)
        this.tbTemplate.removeAllViews()
        this.generateErrorReport()
    }

    fun generateErrorReport() {
        val errorReport =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    ERROR_REPORT,
                    ArrayList::class.java
                ) as? ArrayList<ErrorReport>
            } else {
                @Suppress("UNCHECKED_CAST")
                intent.getSerializableExtra(ERROR_REPORT) as? ArrayList<ErrorReport>
            }

        val row = layoutInflater.inflate(R.layout.table_row_error, null)

        val colLexeme = row.findViewById<TextView>(R.id.colLexeme)
        val colLine = row.findViewById<TextView>(R.id.colLine)
        val colColumn = row.findViewById<TextView>(R.id.colColumn)
        val colType = row.findViewById<TextView>(R.id.colType)
        val colDescription = row.findViewById<TextView>(R.id.colDescription)

        colLexeme.text = "Lexema/Token"
        colLexeme.setTypeface(null, Typeface.BOLD)
        colLine.text = "Linea"
        colLine.setTypeface(null, Typeface.BOLD)
        colColumn.text = "Columna"
        colColumn.setTypeface(null, Typeface.BOLD)
        colType.text = "Tipo"
        colType.setTypeface(null, Typeface.BOLD)
        colDescription.text = "Descripcion"
        colDescription.setTypeface(null, Typeface.BOLD)

        this.tbTemplate.addView(row)

        for (report in errorReport!!) {
            val row = layoutInflater.inflate(R.layout.table_row_error, null)

            val colLexeme = row.findViewById<TextView>(R.id.colLexeme)
            val colLine = row.findViewById<TextView>(R.id.colLine)
            val colColumn = row.findViewById<TextView>(R.id.colColumn)
            val colType = row.findViewById<TextView>(R.id.colType)
            val colDescription = row.findViewById<TextView>(R.id.colDescription)

            colLexeme.text = report.lexeme
            colLine.text = report.line.toString()
            colColumn.text = report.column.toString()
            colType.text = report.type
            colDescription.text = report.description

            this.tbTemplate.addView(row)
        }
    }
}