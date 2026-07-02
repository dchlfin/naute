package com.example.naute

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.naute.databinding.ActivityFoldersBinding

class folders : AppCompatActivity() {

    private lateinit var binding: ActivityFoldersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.preferencesBtn.setOnClickListener {
            val intent = Intent(this, preferences::class.java)
            startActivity(intent)
        }

        binding.folderCard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.folders)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}