package com.example.bubba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Buttons
        val blendButton: ImageView = findViewById(R.id.blendButton)
        val lastsipsButton: ImageView = findViewById(R.id.lastsipsButton)
        val profileButton: Button = findViewById(R.id.profileButton)
        val logoutButton: Button = findViewById(R.id.logoutButton)

        // Navigate to Blend Creation
        blendButton.setOnClickListener {
            startActivity(Intent(this, BlendActivity::class.java))
        }

        // Navigate to Previous Blends
        lastsipsButton.setOnClickListener {
            startActivity(Intent(this, LastSipsActivity::class.java))
        }

        // Navigate to Profile
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Logout and go back to SignIn screen
        logoutButton.setOnClickListener {
            // Optionally clear SharedPreferences here
            val intent = Intent(this, SIgnInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}