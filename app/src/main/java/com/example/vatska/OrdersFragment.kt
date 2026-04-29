package com.example.vatska

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class OrdersFragment : Fragment(), OrdersContract.View {

    private lateinit var pendingContainer: LinearLayout
    private lateinit var completedContainer: LinearLayout
    private lateinit var tvNoPending: TextView
    private lateinit var tvNoCompleted: TextView
    private lateinit var presenter: OrdersContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        pendingContainer   = view.findViewById(R.id.pendingContainer)
        completedContainer = view.findViewById(R.id.completedContainer)
        tvNoPending        = view.findViewById(R.id.tvNoPending)
        tvNoCompleted      = view.findViewById(R.id.tvNoCompleted)

        val prefs = requireContext()
            .getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
        presenter = OrdersPresenter(this, OrderRepository(prefs))

        presenter.loadOrders()
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshOrders()
    }

    // ── OrdersContract.View implementation ───────────────────────────────

    override fun showOrders(pending: List<String>, completed: List<String>) {
        pendingContainer.removeAllViews()
        completedContainer.removeAllViews()

        // Pending section
        if (pending.isEmpty()) {
            tvNoPending.show()
        } else {
            tvNoPending.hide()
            pending.forEachIndexed { index, order ->
                val fields = order.split("|||")
                pendingContainer.addView(
                    buildCard(fields, isBlue = index % 2 == 0, isPending = true) {
                        presenter.onCompleteClicked(order)
                    }
                )
            }
        }

        // Completed section
        if (completed.isEmpty()) {
            tvNoCompleted.show()
        } else {
            tvNoCompleted.hide()
            completed.forEachIndexed { index, order ->
                val fields = order.split("|||")
                completedContainer.addView(
                    buildCard(fields, isBlue = index % 2 == 0, isPending = false) {}
                )
            }
        }
    }

    override fun refreshOrders() {
        presenter.loadOrders()
    }

    // ── Card builder ──────────────────────────────────────────────────────

    private fun buildCard(
        fields: List<String>,
        isBlue: Boolean,
        isPending: Boolean,
        onComplete: () -> Unit
    ): CardView {
        val ctx      = requireContext()
        val name     = fields.getOrElse(0) { "—" }
        val address  = fields.getOrElse(1) { "—" }
        val contact  = fields.getOrElse(2) { "—" }
        val amount   = fields.getOrElse(3) { "—" }
        val note     = fields.getOrElse(4) { "" }
        val status   = fields.getOrElse(5) { "Pending" }

        val cardBg   = if (isBlue) Color.parseColor("#3D5AFE") else Color.WHITE
        val textMain = if (isBlue) Color.WHITE else Color.parseColor("#1A1A2E")
        val textSub  = if (isBlue) Color.WHITE else Color.parseColor("#444444")
        val btnColor = if (isBlue) Color.WHITE else Color.parseColor("#3D5AFE")

        val card = CardView(ctx).apply {
            radius = 24f
            cardElevation = 8f
            setCardBackgroundColor(cardBg)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 24 }
        }

        val inner = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(36, 28, 36, 28)
        }

        // Left column — text
        val textCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        }
        fun tv(text: String, bold: Boolean = false, color: Int = textMain) =
            TextView(ctx).apply {
                this.text = text
                setTextColor(color)
                textSize = 13.5f
                if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 4 }
            }

        textCol.addView(tv("Name: $name", bold = true))
        textCol.addView(tv("Amount: $amount gallons", color = textSub))
        textCol.addView(tv("Status: $status", color = textSub))

        // Right column — buttons
        val btnCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Details button
        btnCol.addView(TextView(ctx).apply {
            text = "Details ›"
            setTextColor(btnColor)
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = if (isPending) 16 else 0 }
            setOnClickListener {
                AlertDialog.Builder(ctx)
                    .setTitle("Order Details")
                    .setMessage(buildString {
                        appendLine("Name:    $name")
                        appendLine("Address: $address")
                        appendLine("Contact: $contact")
                        appendLine("Amount:  $amount gallons")
                        appendLine("Status:  $status")
                        if (note.isNotEmpty()) appendLine("Note:    $note")
                    })
                    .setPositiveButton("Close", null)
                    .show()
            }
        })

        // Complete button — only on pending cards
        if (isPending) {
            btnCol.addView(TextView(ctx).apply {
                text = "Complete"
                setTextColor(
                    if (isBlue) Color.parseColor("#3D5AFE") else Color.WHITE
                )
                textSize = 12f
                background = ctx.getDrawable(
                    if (isBlue) R.drawable.white_button_background
                    else R.drawable.blue_button_background
                )
                setPadding(24, 10, 24, 10)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener { onComplete() }
            })
        }

        inner.addView(textCol)
        inner.addView(btnCol)
        card.addView(inner)
        return card
    }
}