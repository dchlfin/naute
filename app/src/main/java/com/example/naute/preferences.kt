package com.example.naute

import android.os.Bundle
//import android.R.attr.onClick
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.naute.databinding.ActivityPreferencesBinding

class preferences : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding
    private var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferences)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE)
        var nightMODE = sharedPreferences.getBoolean("night", false)

        binding.switcher.isChecked = nightMODE

        binding.switcher.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("night", true).apply()
                binding.previousBtn.setImageResource(R.drawable.previous_dark)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("night", false).apply()
                binding.previousBtn.setImageResource(R.drawable.previous_icon)
            }
            nightMODE = isChecked
        }
        binding.previousBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}