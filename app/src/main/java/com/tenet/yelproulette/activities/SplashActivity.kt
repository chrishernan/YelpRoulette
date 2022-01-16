package com.tenet.yelproulette.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.tenet.yelproulette.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        val typeface : Typeface =
            Typeface.createFromAsset(assets,"fallingsky_jkwk.otf")
        findViewById<TextView>(R.id.textView_splash_app_name).typeface = typeface

        Handler().postDelayed({

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            /*
            // Get the current user id
            val currentUserID = FirestoreClass().getCurrentUserID()
            // Start the Intro Activity

            if (currentUserID.isNotEmpty()) {
                // Start the Main Activity
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // Start the Intro Activity
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }*/


            finish() // Call this when your activity is done and should be closed.
        }, 1000)
    }
}