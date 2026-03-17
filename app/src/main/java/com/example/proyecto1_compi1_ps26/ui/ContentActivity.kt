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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1_compi1_ps26.MainActivity
import com.example.proyecto1_compi1_ps26.R
import androidx.core.net.toUri

class ContentActivity : AppCompatActivity() {

    private var currentState: ScreenState = ScreenState.EDIT

    private var currentFileUri: Uri? = null

    private lateinit var scrollComponents: ScrollView
    private lateinit var dynamicComponentsLayout: LinearLayout

    private lateinit var editStateContainer: LinearLayout
    private lateinit var etFormCode: EditText
    private lateinit var btnSave: Button
    private lateinit var btnSaveAs: Button
    private lateinit var btnApply: Button
    private lateinit var btnFinish: Button

    private lateinit var responseStateContainer: LinearLayout
    private lateinit var btnReturnEdit: Button
    private lateinit var btnSend: Button

    private lateinit var pendingContentToSave: String

    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                currentFileUri = uri
                this.writeFileContent(uri, this.pendingContentToSave)
                Toast.makeText(this, "Archivo guardado correctamente", Toast.LENGTH_SHORT).show()
            }
        }

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
    }

    fun initComponents() {
        this.scrollComponents = findViewById(R.id.scrollComponents)
        this.dynamicComponentsLayout = findViewById(R.id.dynamicComponentsLayout)

        this.editStateContainer = findViewById(R.id.editStateContainer)
        this.etFormCode = findViewById(R.id.etFormCode)
        this.btnSave = findViewById(R.id.btnSave)
        this.btnSaveAs = findViewById(R.id.btnSaveAs)
        this.btnApply = findViewById(R.id.btnApply)
        this.btnFinish = findViewById(R.id.btnFinish)

        this.responseStateContainer = findViewById(R.id.responseStateContainer)
        this.btnReturnEdit = findViewById(R.id.btnReturnEdit)
        this.btnSend = findViewById(R.id.btnSend)
    }

    fun setListeners() {
        this.btnSave.setOnClickListener {
            val content = this.etFormCode.text.toString()
            if (this.currentFileUri != null) {
                this.writeFileContent(this.currentFileUri!!, content)
                Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show()
            } else {
                this.saveAsNewFile(content)
            }
        }

        this.btnSaveAs.setOnClickListener {
            this.saveAsNewFile(this.etFormCode.text.toString())
        }

        this.btnApply.setOnClickListener {
            val code = this.etFormCode.text.toString()
            this.renderComponents(code)
        }

        this.btnFinish.setOnClickListener {
            this.switchState(ScreenState.RESPONSE)
        }

        this.btnReturnEdit.setOnClickListener {
            this.switchState(ScreenState.EDIT)
        }

        this.btnSend.setOnClickListener {
            Toast.makeText(this, "Formulario enviado exitosamente ✓", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyInitialState() {
        val mode = intent.getStringExtra(MainActivity.EXTRA_MODE) ?: MainActivity.EDIT_MODE
        val content = intent.getStringExtra(MainActivity.EXTRA_CONTENT) ?: ""
        val fileUri = intent.getStringExtra(MainActivity.EXTRA_FILE_URI) ?: ""

        if (fileUri.isNotEmpty()) {
            this.currentFileUri = fileUri.toUri()
        }

        this.etFormCode.setText(content)

        if (content.isNotEmpty()) {
            this.renderComponents(content)
        }

        when (mode) {
            MainActivity.RESPONSE_MODE -> this.switchState(ScreenState.RESPONSE)
            else -> this.switchState(ScreenState.EDIT)
        }
    }

    private fun switchState(newState: ScreenState) {
        this.currentState = newState
        when (newState) {
            ScreenState.EDIT -> {
                this.editStateContainer.visibility = View.VISIBLE
                this.responseStateContainer.visibility = View.GONE
            }

            ScreenState.RESPONSE -> {
                this.editStateContainer.visibility = View.GONE
                this.responseStateContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun renderComponents(code: String) {
        this.dynamicComponentsLayout.removeAllViews()

        // Aquí va el parser del .form

        Toast.makeText(this, "Componentes actualizados", Toast.LENGTH_SHORT).show()
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