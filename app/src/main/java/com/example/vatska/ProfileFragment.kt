package com.example.vatska

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // RETRIEVE from SharedPreferences and display
        val prefs = requireContext().getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("userName", "Zedjy Vargas")
        val savedEmail = prefs.getString("userEmail", "zedjynyphomosino.vargas@cit.edu")

        view.findViewById<TextView>(R.id.tvProfileName).text = savedName
        view.findViewById<TextView>(R.id.tvProfileEmail).text = savedEmail

        // CLEAR SharedPreferences on logout
        view.findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}