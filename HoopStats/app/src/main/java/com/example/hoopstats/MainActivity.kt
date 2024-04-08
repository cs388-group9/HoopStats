package com.example.hoopstats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Start GamesMain activity
        startActivity(Intent(this, GamesMain::class.java))

        // Finish current activity (optional, depends on your navigation flow)
        finish()


    }
}
