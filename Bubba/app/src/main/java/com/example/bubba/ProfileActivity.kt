package com.example.bubba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Elements
    private lateinit var usernameText: TextView
    private lateinit var emailText: TextView
    private lateinit var favoriteBlendValue: TextView
    private lateinit var profileImage: ImageView
    private lateinit var favoriteBlendImage: ImageView
    private lateinit var editProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind UI elements
        usernameText = findViewById(R.id.usernameText)
        emailText = findViewById(R.id.emailText)
        favoriteBlendValue = findViewById(R.id.favoriteBlendValue)
        profileImage = findViewById(R.id.profileImage)
        favoriteBlendImage = findViewById(R.id.favoriteBlend)
        editProfileButton = findViewById(R.id.editProfileButton)

        loadUserProfile()

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            emailText.text = currentUser.email

            // ✅ Load favorite blend instantly from SharedPreferences
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val flavor = prefs.getString("favorite_flavor", null)
            val size = prefs.getString("favorite_size", null)
            val sugar = prefs.getString("favorite_sugar", null)
            val ice = prefs.getString("favorite_ice", null)
            val addOns = prefs.getStringSet("favorite_addons", emptySet()) ?: emptySet()
            val imageResId = prefs.getInt("favorite_image", R.drawable.pfpimageplaceholder)

            if (flavor != null && size != null && sugar != null && ice != null) {
                val addOnsText = if (addOns.isEmpty()) "no add-ons" else addOns.joinToString(", ")
                val summary = "A $size $flavor boba tea with $sugar, $ice, and $addOnsText."
                favoriteBlendValue.text = summary
                favoriteBlendImage.setImageResource(imageResId)
            } else {
                favoriteBlendValue.text = "No favorite blend saved."
                favoriteBlendImage.setImageResource(R.drawable.pfpimageplaceholder)
            }

            // 🔄 Load username from Firebase separately
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username") ?: "Bubba Fan"
                        usernameText.text = username
                    }
                }
                .addOnFailureListener {
                    usernameText.text = "User"
                }
        }
    }
}
