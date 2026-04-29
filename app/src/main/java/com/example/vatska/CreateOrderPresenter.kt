package com.example.vatska

import android.widget.EditText

class CreateOrderPresenter(
    private val view: CreateOrderContract.View,
    private val repository: OrderRepository
) : CreateOrderContract.Presenter {

    override fun onSaveClicked(
        name: String, address: String,
        contact: String, amount: String, note: String,
        nameField: EditText, addressField: EditText,
        contactField: EditText, amountField: EditText
    ) {
        var hasError = false

        if (name.isEmpty()) {
            view.highlightError(nameField)
            hasError = true
        } else {
            view.resetField(nameField)
        }

        if (address.isEmpty()) {
            view.highlightError(addressField)
            hasError = true
        } else {
            view.resetField(addressField)
        }

        if (contact.isEmpty()) {
            view.highlightError(contactField)
            hasError = true
        } else {
            view.resetField(contactField)
        }

        if (amount.isEmpty()) {
            view.highlightError(amountField)
            hasError = true
        } else {
            view.resetField(amountField)
        }

        if (hasError) return

        repository.saveOrder(name, address, contact, amount, note)
        view.showSuccess("Order saved!")
        view.clearForm()
    }

    override fun onClearClicked() {
        view.clearForm()
    }
}