package com.example.proyecto1_compi1_ps26

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1_compi1_ps26.ui.ContentActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "mode"
        const val RESPONSE_MODE = "response"
        const val EDIT_MODE = "edit"
        const val SAVED_MODE = "saved"
        const val EXTRA_CONTENT = "content"
        const val EXTRA_FILE_URI = "file_uri"
    }

    private val openFormLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val content = this.readFileContent(uri)
                this.navigateToContent(
                    RESPONSE_MODE,
                    content,
                    uri.toString()
                )
            }
        }

    private val openCodeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val content = this.readFileContent(uri)
                this.navigateToContent(
                    EDIT_MODE,
                    content,
                    uri.toString()
                )
            }
        }

    private val openSavedFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                navigateToContent(
                    SAVED_MODE,
                    readFileContent(uri),
                    uri.toString()
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnOpenForm = findViewById<Button>(R.id.btnOpenForm)
        val btnOpenSaveFile = findViewById<Button>(R.id.btnOpenSaveFile)
        val btnOpenCode = findViewById<Button>(R.id.btnOpenCode)
        val btnCreateForm = findViewById<Button>(R.id.btnCreateForm)

        btnOpenForm.setOnClickListener {
            this.openFilePicker(openFormLauncher)
        }

        btnOpenSaveFile.setOnClickListener {
            this.openFilePicker(this.openSavedFileLauncher)
        }

        btnOpenCode.setOnClickListener {
            this.openFilePicker(openCodeLauncher)
        }

        btnCreateForm.setOnClickListener {
            this.navigateToContent(EDIT_MODE)
        }
    }

    private fun openFilePicker(
        launcher: androidx.activity.result.ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("*/*"))
        }
        launcher.launch(intent)
    }

    private fun readFileContent(uri: Uri): String {
        return try {
            contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
        } catch (e: Exception) {
            Toast.makeText(this, "Error al leer el archivo: ${e.message}", Toast.LENGTH_LONG).show()
            ""
        }
    }

    private fun navigateToContent(
        mode: String,
        content: String = "",
        fileUri: String = ""
    ) {
        val intent = Intent(this, ContentActivity::class.java).apply {
            putExtra(EXTRA_MODE, mode)
            putExtra(EXTRA_CONTENT, content)
            putExtra(EXTRA_FILE_URI, fileUri)
        }
        startActivity(intent)
    }

}