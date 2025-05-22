package com.example.bubba

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.google.firebase.FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val greyscaleImage = findViewById<ImageView>(R.id.greyscaleImage)
        val colorImage = findViewById<ImageView>(R.id.colorImage)

        // Apply greyscale filter to the top image
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        greyscaleImage.colorFilter = ColorMatrixColorFilter(colorMatrix)

        // Wait for layout to be measured
        colorImage.post {
            val imageHeight = colorImage.height

            // Clip reveal from bottom to top
            val animator = ValueAnimator.ofInt(imageHeight, 0)
            animator.duration = 3000
            animator.interpolator = DecelerateInterpolator()

            animator.addUpdateListener { valueAnimator ->
                val revealHeight = valueAnimator.animatedValue as Int
                colorImage.clipBounds = Rect(0, revealHeight, colorImage.width, imageHeight)
            }

            animator.start()

            // Wait until animation is done, then check auth and navigate
            Handler(Looper.getMainLooper()).postDelayed({
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    // User is signed in — go to main screen
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // User is not signed in — go to sign-in
                    startActivity(Intent(this, SIgnInActivity::class.java))
                }
                finish()
            }, 3200)
        }
    }
}
