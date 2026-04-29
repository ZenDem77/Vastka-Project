package com.example.vatska

import android.widget.EditText

interface CreateOrderContract {

    interface View {
        fun highlightError(field: EditText)
        fun resetField(field: EditText)
        fun clearForm()
        fun showSuccess(message: String)
    }

    interface Presenter {
        fun onSaveClicked(
            name: String, address: String,
            contact: String, amount: String, note: String,
            nameField: EditText, addressField: EditText,
            contactField: EditText, amountField: EditText
        )
        fun onClearClicked()
    }
}