package com.example.vatska

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class CreateOrderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_order, container, false)

        val etClientName = view.findViewById<EditText>(R.id.etClientName)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val etContact = view.findViewById<EditText>(R.id.etContact)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val btnClear = view.findViewById<Button>(R.id.btnClear)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val prefs = requireContext().getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)

        // RETRIEVE last saved order data and pre-fill if available
        etClientName.setText(prefs.getString("lastClientName", ""))
        etAddress.setText(prefs.getString("lastAddress", ""))
        etContact.setText(prefs.getString("lastContact", ""))

        btnSave.setOnClickListener {
            val clientName = etClientName.text.toString()
            val address = etAddress.text.toString()
            val contact = etContact.text.toString()

            // SAVE last order details to SharedPreferences
            prefs.edit()
                .putString("lastClientName", clientName)
                .putString("lastAddress", address)
                .putString("lastContact", contact)
                .apply()

            // Visual feedback
            btnSave.text = "Saved!"
            btnSave.postDelayed({ btnSave.text = "Save" }, 1500)
        }

        btnClear.setOnClickListener {
            // CLEAR the last saved order fields
            prefs.edit()
                .remove("lastClientName")
                .remove("lastAddress")
                .remove("lastContact")
                .apply()

            etClientName.setText("")
            etAddress.setText("")
            etContact.setText("")
            etAmount.setText("")
            etNote.setText("")
        }

        return view
    }
}