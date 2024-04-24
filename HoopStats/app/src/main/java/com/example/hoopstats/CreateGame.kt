package com.example.hoopstats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.models.Game
import com.example.hoopstats.models.Team
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CreateGame : AppCompatActivity() {
    private lateinit var gameNameEditText: EditText
    private lateinit var team1NameEditText: EditText
    private lateinit var team2NameEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        // Initialize UI components
        gameNameEditText = findViewById(R.id.gameNameEditText)
        team1NameEditText = findViewById(R.id.teamName1EditText)
        team2NameEditText = findViewById(R.id.teamName2EditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val gameName = gameNameEditText.text.toString().trim()
            val team1Name = team1NameEditText.text.toString().trim()
            val team2Name = team2NameEditText.text.toString().trim()
            if (gameName.isNotEmpty() && team1Name.isNotEmpty() && team2Name.isNotEmpty()) {
                createGameWithTeams(gameName, team1Name, team2Name)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createGameWithTeams(gameName: String, team1Name: String, team2Name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference()
        val gameId = generateShortId()  // Use the custom short ID function
        val team1Id = databaseRef.child("teams").push().key ?: return
        val team2Id = databaseRef.child("teams").push().key ?: return

        val team1 = Team(
            teamId = team1Id,
            teamName = team1Name,
            playerIds = mutableListOf(),
            gameId = gameId,
            rebounds = 0,
            assists = 0,
            twoPointers = 0,
            threePointers = 0,
            freeThrows = 0,
            totalPoints = 0
        )

        val team2 = Team(
            teamId = team2Id,
            teamName = team2Name,
            playerIds = mutableListOf(),
            gameId = gameId,
            rebounds = 0,
            assists = 0,
            twoPointers = 0,
            threePointers = 0,
            freeThrows = 0,
            totalPoints = 0
        )

        val game = Game(gameId, gameName, listOf(team1Id, team2Id), userId,submittedByUserIds = emptyList())

        // Prepare the combined data map
        val updates = mapOf(
            "teams/$team1Id" to team1,
            "teams/$team2Id" to team2,
            "games/$gameId" to game
        )

        // Execute the multi-path update
        databaseRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Game and teams created successfully!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to create game and teams.", Toast.LENGTH_LONG).show()
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
}
