package com.example.hoopstats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Random

class CreateGame : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        val gameNameEditText: EditText = findViewById(R.id.gameNameEditText)
        val teamName1EditText: EditText = findViewById(R.id.teamName1EditText)
        val teamName2EditText: EditText = findViewById(R.id.teamName2EditText)
        val submitButton: Button = findViewById(R.id.submitButton)

        database = FirebaseDatabase.getInstance().reference

        submitButton.setOnClickListener {
            val gameName = gameNameEditText.text.toString().trim()
            val teamNames = listOf(teamName1EditText.text.toString().trim(), teamName2EditText.text.toString().trim())

            if (gameName.isNotEmpty() && teamNames.all { it.isNotEmpty() }) {
                createGameWithUniqueID(gameName, teamNames)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateShortId(length: Int = 6): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { Random().nextInt(chars.length) }
            .map(chars::get)
            .joinToString("")
    }

    private fun createGameWithUniqueID(gameName: String, teamNames: List<String>, attempt: Int = 0) {
        val maxAttempts = 10
        if (attempt >= maxAttempts) {
            Toast.makeText(this, "Failed to generate a unique game ID. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Authentication failed. User is not logged in.", Toast.LENGTH_LONG).show()
            return
        }

        val gameId = generateShortId()
        val newGame = Game(gameId, gameName, teamNames, creatorUserId = currentUser.uid)

        database.child("games").child(gameId).setValue(newGame)
            .addOnSuccessListener {
                Toast.makeText(this@CreateGame, "Game created successfully with ID: $gameId", Toast.LENGTH_LONG).show()
                // Optionally, navigate to another activity or perform additional operations
            }
            .addOnFailureListener {
                Toast.makeText(this@CreateGame, "Failed to create game", Toast.LENGTH_SHORT).show()
            }
    }
}
