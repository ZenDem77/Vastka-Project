package com.example.vatska

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vatska.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                // SAVE to SharedPreferences
                val prefs = getSharedPreferences("VatskaPrefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userEmail", email)
                    .putString("userName", name)
                    .apply()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
                finish()
            } else {
                binding.etName.error = "Please enter your name"
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            finish()
        }
    }
}