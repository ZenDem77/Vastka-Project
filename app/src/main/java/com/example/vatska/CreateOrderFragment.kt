package com.example.vatska

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class CreateOrderFragment : Fragment(), CreateOrderContract.View {

    private lateinit var etClientName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etContact: EditText
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var presenter: CreateOrderContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_order, container, false)

        etClientName = view.findViewById(R.id.etClientName)
        etAddress    = view.findViewById(R.id.etAddress)
        etContact    = view.findViewById(R.id.etContact)
        etAmount     = view.findViewById(R.id.etAmount)
        etNote       = view.findViewById(R.id.etNote)

        val prefs = requireContext()
            .getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
        presenter = CreateOrderPresenter(this, OrderRepository(prefs))

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            presenter.onSaveClicked(
                etClientName.value(), etAddress.value(),
                etContact.value(),    etAmount.value(),
                etNote.value(),
                etClientName, etAddress, etContact, etAmount
            )
        }

        view.findViewById<Button>(R.id.btnClear).setOnClickListener {
            presenter.onClearClicked()
        }

        return view
    }


    override fun highlightError(field: EditText) {
        field.showError()
    }

    override fun resetField(field: EditText) {
        field.clearError()
    }

    override fun clearForm() {
        etClientName.setText("")
        etAddress.setText("")
        etContact.setText("")
        etAmount.setText("")
        etNote.setText("")

        etClientName.clearError()
        etAddress.clearError()
        etContact.clearError()
        etAmount.clearError()
    }

    override fun showSuccess(message: String) {
        requireContext().toast(message)
    }
}