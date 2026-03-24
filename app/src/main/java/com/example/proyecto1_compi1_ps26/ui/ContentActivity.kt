package com.example.proyecto1_compi1_ps26.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1_compi1_ps26.MainActivity
import com.example.proyecto1_compi1_ps26.R
import androidx.core.net.toUri
import com.example.proyecto1_compi1_ps26.domain.analyzers.form_creation.FormAnalyzer
import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport
import com.example.proyecto1_compi1_ps26.domain.entities.FormRenderer
import com.example.proyecto1_compi1_ps26.domain.translation.PkmDocument
import com.example.proyecto1_compi1_ps26.domain.translation.PkmTranslator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContentActivity : AppCompatActivity() {

    companion object {
        const val ERROR_REPORT = "error_report"
    }

    private var currentState: ScreenState = ScreenState.EDIT

    private var currentFileUri: Uri? = null

    private var pendingExportAuthor: String = ""
    private var pendingExportDescription: String = ""

    private lateinit var scrollComponents: ScrollView
    private lateinit var dynamicComponentsLayout: LinearLayout
    private lateinit var sharedInputContainer: LinearLayout
    private lateinit var etCode: EditText

    private lateinit var editStateContainer: LinearLayout
    private lateinit var btnSave: FloatingActionButton
    private lateinit var btnSaveAs: FloatingActionButton
    private lateinit var btnApplyEdit: FloatingActionButton
    private lateinit var btnFinish: FloatingActionButton
    private lateinit var btnReportEdit: FloatingActionButton
    private lateinit var btnExport: FloatingActionButton

    private lateinit var responseStateContainer: LinearLayout
    private lateinit var btnReturnEdit: Button
    private lateinit var btnSend: Button

    private lateinit var savedStateContainer: LinearLayout
    private lateinit var btnApplySaved: Button
    private lateinit var btnReportSaved: Button

    private lateinit var pendingContentToSave: String

    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                currentFileUri = uri
                this.writeFileContent(uri, this.pendingContentToSave)
                Toast.makeText(this, "Archivo .form guardado correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val createPkmFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                writePkmFile(
                    uri,
                    pendingExportAuthor,
                    pendingExportDescription
                )
                Toast.makeText(this, "Archivo .pkm exportado correctamente", Toast.LENGTH_SHORT)
                    .show()

                pendingExportAuthor = ""
                pendingExportDescription = ""
            }
        }

    var errorReport: ArrayList<ErrorReport> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.initComponents()
        this.setListeners()
        this.applyInitialState()
    }

    fun initComponents() {
        this.scrollComponents = findViewById(R.id.scrollComponents)
        this.dynamicComponentsLayout = findViewById(R.id.dynamicComponentsLayout)
        this.sharedInputContainer = findViewById(R.id.sharedInputContainer)
        this.etCode = findViewById(R.id.etCode)

        this.editStateContainer = findViewById(R.id.editStateContainer)
        this.btnSave = findViewById(R.id.btnSave)
        this.btnSaveAs = findViewById(R.id.btnSaveAs)
        this.btnApplyEdit = findViewById(R.id.btnApplyEdit)
        this.btnFinish = findViewById(R.id.btnFinish)
        this.btnReportEdit = findViewById(R.id.btnReportEdit)
        this.btnReportEdit.isEnabled = false
        this.btnExport = findViewById(R.id.btnExport)

        this.responseStateContainer = findViewById(R.id.responseStateContainer)
        this.btnReturnEdit = findViewById(R.id.btnReturnEdit)
        this.btnSend = findViewById(R.id.btnSend)

        this.savedStateContainer = findViewById(R.id.savedStateContainer)
        this.btnApplySaved = findViewById(R.id.btnApplySaved)
        this.btnReportSaved = findViewById(R.id.btnReportSaved)
        this.btnReportSaved.isEnabled = false
    }

    fun setListeners() {
        this.btnSave.setOnClickListener {
            val content = this.etCode.text.toString()
            if (this.currentFileUri != null) {
                this.writeFileContent(this.currentFileUri!!, content)
                Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show()
            } else {
                this.saveAsNewFile(content)
            }
        }

        this.btnSaveAs.setOnClickListener {
            this.saveAsNewFile(this.etCode.text.toString())
        }

        this.btnApplyEdit.setOnClickListener {
            val code = this.etCode.text.toString()
            this.renderComponents(code)
        }

        this.btnFinish.setOnClickListener {
            this.switchState(ScreenState.RESPONSE)
        }

        this.btnReportEdit.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)
            intent.putExtra(ERROR_REPORT, this.errorReport)
            startActivity(intent)
        }

        this.btnExport.setOnClickListener {
            showExportDialog()
        }

        this.btnReturnEdit.setOnClickListener {
            this.switchState(ScreenState.EDIT)
        }

        this.btnSend.setOnClickListener {
            Toast.makeText(this, "Formulario enviado exitosamente", Toast.LENGTH_LONG).show()
        }

        this.btnApplySaved.setOnClickListener {
            // HACER EL ANALISIS DEL CODIGO .pkm
        }

        this.btnReportSaved.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)
            intent.putExtra(ERROR_REPORT, this.errorReport)
            startActivity(intent)
        }
    }

    private fun applyInitialState() {
        val mode = intent.getStringExtra(MainActivity.EXTRA_MODE) ?: MainActivity.EDIT_MODE
        val content = intent.getStringExtra(MainActivity.EXTRA_CONTENT) ?: ""
        val fileUri = intent.getStringExtra(MainActivity.EXTRA_FILE_URI) ?: ""

        if (fileUri.isNotEmpty()) {
            this.currentFileUri = fileUri.toUri()
        }

        this.etCode.setText(content)

        if (content.isNotEmpty() && mode != MainActivity.SAVED_MODE) {
            this.renderComponents(content)
        }

        when (mode) {
            MainActivity.RESPONSE_MODE -> this.switchState(ScreenState.RESPONSE)
            MainActivity.SAVED_MODE -> this.switchState(ScreenState.SAVED)
            else -> this.switchState(ScreenState.EDIT)
        }
    }

    private fun switchState(newState: ScreenState) {
        this.currentState = newState
        this.sharedInputContainer.visibility = View.GONE
        this.editStateContainer.visibility = View.GONE
        this.responseStateContainer.visibility = View.GONE
        this.savedStateContainer.visibility = View.GONE
        when (newState) {
            ScreenState.EDIT -> {
                this.sharedInputContainer.visibility = View.VISIBLE
                this.editStateContainer.visibility = View.VISIBLE
            }

            ScreenState.RESPONSE -> {
                this.responseStateContainer.visibility = View.VISIBLE
            }

            ScreenState.SAVED -> {
                this.sharedInputContainer.visibility = View.VISIBLE
                this.savedStateContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun showExportDialog() {
        val dialog = SaveDialog.newInstance()

        dialog.onExportConfirmed = { author, description ->
            pendingExportAuthor = author
            pendingExportDescription = description

            this.launchPkmFilePicker()
        }

        dialog.show(supportFragmentManager, SaveDialog.TAG)
    }

    private fun launchPkmFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "guardado.pkm")
        }
        this.createPkmFileLauncher.launch(intent)
    }

    private fun writePkmFile(
        uri: Uri,
        author: String,
        description: String
    ) {
        val content = this.buildPkmContent(
            author,
            description
        )
        this.writeFileContent(uri, content)
    }

    private fun buildPkmContent(
        author: String,
        description: String
    ): String {
        val analyzer = FormAnalyzer()
        val result: String = analyzer.analyze(this.etCode.text.toString())
        if (analyzer.errors.isEmpty()) {
            this.btnReportEdit.isEnabled = false
        } else {
            Toast.makeText(
                this,
                "Se encontraron errores en el codigo. Revise el Reporte de Errores",
                Toast.LENGTH_LONG
            )
                .show()
            this.errorReport = analyzer.errors
            this.btnReportEdit.isEnabled = true
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Mensaje!!!")
        builder.setMessage(result)
        builder.setPositiveButton("Aceptar") { dialog, which -> }
        builder.show()
        val doc: PkmDocument = PkmTranslator(author, description).translate(analyzer.interpreter)
        return doc.generateContent()
    }

    private fun renderComponents(code: String) {
        this.dynamicComponentsLayout.removeAllViews()
        val analyzer = FormAnalyzer()
        val result: String = analyzer.analyze(code)
        if (analyzer.errors.isEmpty()) {
            this.btnReportEdit.isEnabled = false
        } else {
            Toast.makeText(
                this,
                "Se encontraron errores en el codigo. Revise el Reporte de Errores",
                Toast.LENGTH_LONG
            )
                .show()
            this.errorReport = analyzer.errors
            this.btnReportEdit.isEnabled = true
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Mensaje!!!")
        builder.setMessage(result)
        builder.setPositiveButton("Aceptar") { dialog, which -> }
        builder.show()
        val render = FormRenderer(this)
        render.render(analyzer.interpreter.formOutput, this.dynamicComponentsLayout)
    }

    private fun saveAsNewFile(content: String) {
        this.pendingContentToSave = content
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "formulario.form")
        }
        this.createFileLauncher.launch(intent)
    }

    private fun writeFileContent(uri: Uri, content: String) {
        try {
            contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use { writer ->
                writer.write(content)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}