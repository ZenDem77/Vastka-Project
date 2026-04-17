package com.example.vatska

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class CreateOrderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_order, container, false)

        val etClientName = view.findViewById<EditText>(R.id.etClientName)
        val etAddress    = view.findViewById<EditText>(R.id.etAddress)
        val etContact    = view.findViewById<EditText>(R.id.etContact)
        val etAmount     = view.findViewById<EditText>(R.id.etAmount)
        val etNote       = view.findViewById<EditText>(R.id.etNote)
        val btnClear     = view.findViewById<Button>(R.id.btnClear)
        val btnSave      = view.findViewById<Button>(R.id.btnSave)

        val prefs = requireContext().getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)

        // ── CLEAR button ──────────────────────────────────────────────────
        btnClear.setOnClickListener {
            etClientName.setText("")
            etAddress.setText("")
            etContact.setText("")
            etAmount.setText("")
            etNote.setText("")
            // Reset red borders
            resetBorder(etClientName)
            resetBorder(etAddress)
            resetBorder(etContact)
            resetBorder(etAmount)
        }

        // ── SAVE button ───────────────────────────────────────────────────
        btnSave.setOnClickListener {
            val clientName = etClientName.text.toString().trim()
            val address    = etAddress.text.toString().trim()
            val contact    = etContact.text.toString().trim()
            val amount     = etAmount.text.toString().trim()
            val note       = etNote.text.toString().trim()

            // Validate required fields
            var hasError = false

            if (clientName.isEmpty()) { highlightError(etClientName); hasError = true }
            else resetBorder(etClientName)

            if (address.isEmpty()) { highlightError(etAddress); hasError = true }
            else resetBorder(etAddress)

            if (contact.isEmpty()) { highlightError(etContact); hasError = true }
            else resetBorder(etContact)

            if (amount.isEmpty()) { highlightError(etAmount); hasError = true }
            else resetBorder(etAmount)

            if (hasError) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Build order string: fields separated by "|||"
            // Format: clientName|||address|||contact|||amount|||note|||status
            val newOrder = "$clientName|||$address|||$contact|||$amount|||$note|||Pending"

            // Load existing orders, append new one, save back
            val existing = prefs.getString("orderList", "") ?: ""
            val updated  = if (existing.isEmpty()) newOrder else "$existing\n$newOrder"
            prefs.edit().putString("orderList", updated).apply()

            Toast.makeText(requireContext(), "Order saved!", Toast.LENGTH_SHORT).show()

            // Clear all fields on success
            etClientName.setText("")
            etAddress.setText("")
            etContact.setText("")
            etAmount.setText("")
            etNote.setText("")
            resetBorder(etClientName)
            resetBorder(etAddress)
            resetBorder(etContact)
            resetBorder(etAmount)
        }

        return view
    }

    private fun highlightError(field: EditText) {
        field.setBackgroundResource(R.drawable.input_error_background)
    }

    private fun resetBorder(field: EditText) {
        field.setBackgroundResource(R.drawable.input_background)
    }
}