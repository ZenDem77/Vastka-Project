package com.example.vatska

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast

fun Context.toast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun View.hide() { visibility = View.GONE }

fun View.show() { visibility = View.VISIBLE }

fun EditText.value(): String = text.toString().trim()

fun EditText.showError() {
    setBackgroundResource(R.drawable.input_error_background)
}

    fun EditText.clearError() {
    setBackgroundResource(R.drawable.input_background)
}