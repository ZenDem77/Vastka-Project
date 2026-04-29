package com.example.vatska

import android.content.SharedPreferences

class OrderRepository(private val prefs: SharedPreferences) {

    fun saveOrder(
        name: String, address: String,
        contact: String, amount: String, note: String
    ) {
        val newEntry = "$name|||$address|||$contact|||$amount|||$note|||Pending"
        val existing = prefs.getString("orderList", "") ?: ""
        val updated  = if (existing.isEmpty()) newEntry else "$existing\n$newEntry"
        prefs.edit().putString("orderList", updated).apply()
    }

    fun getOrders(): List<String> {
        val raw = prefs.getString("orderList", "") ?: ""
        return if (raw.isEmpty()) emptyList() else raw.split("\n")
    }

    fun markAsCompleted(orderString: String) {
        val orders = getOrders().toMutableList()
        val index  = orders.indexOf(orderString)
        if (index != -1) {
            orders[index] = orderString
                .removeSuffix("|||Pending") + "|||Completed"
            prefs.edit()
                .putString("orderList", orders.joinToString("\n"))
                .apply()
        }
    }
}