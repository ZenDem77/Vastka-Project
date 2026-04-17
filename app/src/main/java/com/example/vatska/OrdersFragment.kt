package com.example.vatska

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class OrdersFragment : Fragment() {

    private lateinit var pendingContainer: LinearLayout
    private lateinit var completedContainer: LinearLayout
    private lateinit var tvNoPending: TextView
    private lateinit var tvNoCompleted: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        pendingContainer   = view.findViewById(R.id.pendingContainer)
        completedContainer = view.findViewById(R.id.completedContainer)
        tvNoPending        = view.findViewById(R.id.tvNoPending)
        tvNoCompleted      = view.findViewById(R.id.tvNoCompleted)

        loadOrders()

        return view
    }

    // ── Called every time the fragment becomes visible ────────────────────
    override fun onResume() {
        super.onResume()
        pendingContainer.removeAllViews()
        completedContainer.removeAllViews()
        loadOrders()
    }

    private fun loadOrders() {
        val prefs    = requireContext().getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
        val raw      = prefs.getString("orderList", "") ?: ""
        val orders   = if (raw.isEmpty()) emptyList() else raw.split("\n")

        val pending   = orders.filter { it.endsWith("|||Pending") }
        val completed = orders.filter { it.endsWith("|||Completed") }

        // Pending
        if (pending.isEmpty()) {
            tvNoPending.visibility = View.VISIBLE
        } else {
            tvNoPending.visibility = View.GONE
            pending.forEachIndexed { index, order ->
                val fields = order.split("|||")
                // fields: [0]name [1]address [2]contact [3]amount [4]note [5]status
                val isBlue = index % 2 == 0
                pendingContainer.addView(buildOrderCard(fields, isBlue, isPending = true) {
                    markAsCompleted(order)
                })
            }
        }

        // Completed
        if (completed.isEmpty()) {
            tvNoCompleted.visibility = View.VISIBLE
        } else {
            tvNoCompleted.visibility = View.GONE
            completed.forEachIndexed { index, order ->
                val fields = order.split("|||")
                val isBlue = index % 2 == 0
                completedContainer.addView(buildOrderCard(fields, isBlue, isPending = false) {})
            }
        }
    }

    private fun buildOrderCard(
        fields: List<String>,
        isBlue: Boolean,
        isPending: Boolean,
        onComplete: () -> Unit
    ): CardView {
        val context = requireContext()

        val name    = fields.getOrElse(0) { "—" }
        val address = fields.getOrElse(1) { "—" }
        val contact = fields.getOrElse(2) { "—" }
        val amount  = fields.getOrElse(3) { "—" }
        val note    = fields.getOrElse(4) { "" }
        val status  = fields.getOrElse(5) { "Pending" }

        val cardBg   = if (isBlue) Color.parseColor("#3D5AFE") else Color.WHITE
        val textMain = if (isBlue) Color.WHITE else Color.parseColor("#1A1A2E")
        val textSub  = if (isBlue) Color.WHITE else Color.parseColor("#444444")
        val btnColor = if (isBlue) Color.WHITE else Color.parseColor("#3D5AFE")

        // ── Card ──────────────────────────────────────────────────────────
        val card = CardView(context).apply {
            radius = 24f
            cardElevation = 8f
            setCardBackgroundColor(cardBg)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = 24
            layoutParams = lp
        }

        // ── Inner layout ──────────────────────────────────────────────────
        val inner = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(36, 28, 36, 28)
        }

        // ── Left: text info ───────────────────────────────────────────────
        val textCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        fun makeText(content: String, bold: Boolean = false, color: Int = textMain, size: Float = 13.5f): TextView {
            return TextView(context).apply {
                text = content
                setTextColor(color)
                textSize = size
                if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.bottomMargin = 4
                layoutParams = lp
            }
        }

        textCol.addView(makeText("Name: $name", bold = true, size = 14f))
        textCol.addView(makeText("Amount: $amount gallons", color = textSub))
        textCol.addView(makeText("Status: $status", color = textSub))

        // ── Right: buttons ────────────────────────────────────────────────
        val btnCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Details button
        val btnDetails = TextView(context).apply {
            text = "Details ›"
            setTextColor(btnColor)
            textSize = 13f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = if (isPending) 16 else 0
            layoutParams = lp
            setOnClickListener {
                showDetailsDialog(name, address, contact, amount, note, status)
            }
        }
        btnCol.addView(btnDetails)

        // Complete button (only for pending orders)
        if (isPending) {
            val btnComplete = TextView(context).apply {
                text = "Complete"
                setTextColor(if (isBlue) Color.parseColor("#3D5AFE") else Color.WHITE)
                textSize = 12f
                background = context.getDrawable(
                    if (isBlue) R.drawable.white_button_background
                    else R.drawable.blue_button_background
                )
                setPadding(24, 10, 24, 10)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener { onComplete() }
            }
            btnCol.addView(btnComplete)
        }

        inner.addView(textCol)
        inner.addView(btnCol)
        card.addView(inner)

        return card
    }

    private fun showDetailsDialog(
        name: String, address: String, contact: String,
        amount: String, note: String, status: String
    ) {
        val message = buildString {
            appendLine("Name:     $name")
            appendLine("Address:  $address")
            appendLine("Contact:  $contact")
            appendLine("Amount:   $amount gallons")
            appendLine("Status:   $status")
            if (note.isNotEmpty()) appendLine("Note:     $note")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun markAsCompleted(orderString: String) {
        val prefs   = requireContext().getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
        val raw     = prefs.getString("orderList", "") ?: ""
        val orders  = (if (raw.isEmpty()) emptyList() else raw.split("\n").toMutableList()).toMutableList()

        val index = orders.indexOf(orderString)
        if (index != -1) {
            // Replace "|||Pending" with "|||Completed"
            orders[index] = orderString.removeSuffix("|||Pending") + "|||Completed"
            prefs.edit().putString("orderList", orders.joinToString("\n")).apply()
        }

        // Refresh the UI
        pendingContainer.removeAllViews()
        completedContainer.removeAllViews()
        loadOrders()
    }
}