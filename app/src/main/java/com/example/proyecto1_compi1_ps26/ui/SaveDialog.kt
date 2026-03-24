package com.example.proyecto1_compi1_ps26.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyecto1_compi1_ps26.R
import com.google.android.material.textfield.TextInputEditText

class SaveDialog : DialogFragment() {

    companion object {
        const val TAG = "ExportDialogFragment"

        fun newInstance(): SaveDialog {
            return SaveDialog()
        }
    }

    var onExportConfirmed: ((author: String, description: String) -> Unit)? = null

    private lateinit var etAuthor: TextInputEditText
    private lateinit var etDescription: TextInputEditText

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_save, null)

        etAuthor = view.findViewById(R.id.etAuthor)
        etDescription = view.findViewById(R.id.etDescription)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Exportar a .pkm")
            .setView(view)
            .setPositiveButton("Exportar", null)
            .setNegativeButton("Cancelar") { _, _ -> dismiss() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (validateFields()) {
                    val author = etAuthor.text.toString().trim()
                    val description = etDescription.text.toString().trim()
                    onExportConfirmed?.invoke(author, description)
                    dismiss()
                }
            }
        }

        return dialog
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val author = etAuthor.text.toString().trim()
        if (author.isEmpty()) {
            Toast.makeText(requireContext(), "El autor es obligatorio", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val description = etDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "La descripción es obligatoria", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

}