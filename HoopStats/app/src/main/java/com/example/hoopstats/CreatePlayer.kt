package com.example.hoopstats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.hoopstats.models.Player

class CreatePlayerActivity : AppCompatActivity() {
    private lateinit var playerNameEditText: EditText
    private lateinit var team1NameTextView: TextView
    private lateinit var team2NameTextView: TextView
    private lateinit var teamSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_player)

        playerNameEditText = findViewById(R.id.playerNameEditText)
        team1NameTextView = findViewById(R.id.team1NameTextView)
        team2NameTextView = findViewById(R.id.team2NameTextView)
        teamSwitch = findViewById(R.id.teamSwitch)
        val submitButton = findViewById<Button>(R.id.submitPlayerButton)

        // Retrieve team names and IDs from the intent
        team1NameTextView.text = intent.getStringExtra("team1Name") ?: "Team 1"
        team2NameTextView.text = intent.getStringExtra("team2Name") ?: "Team 2"
        val team1Id = intent.getStringExtra("team1Id")
        val team2Id = intent.getStringExtra("team2Id")

        submitButton.setOnClickListener {
            val playerName = playerNameEditText.text.toString().trim()
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Please enter a player name.", Toast.LENGTH_SHORT).show()
            } else {
                val selectedTeamId = if (teamSwitch.isChecked) team2Id else team1Id
                if (selectedTeamId != null) {
                    savePlayerToDatabase(playerName, selectedTeamId)
                } else {
                    Toast.makeText(this, "Error: Team ID is not available.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun savePlayerToDatabase(playerName: String, teamId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val playerKey = database.child("players").child(teamId).push().key // Generate a unique key for the player under the team ID

        if (playerKey == null) {
            Toast.makeText(this, "Could not generate player key.", Toast.LENGTH_LONG).show()
            return
        }

        val playerId = playerKey // Store the generated player key as the playerId

        val newPlayer = Player(playerName, teamId, 0, 0, 0, 0, 0, playerId) // Creating a new player with default stats and team ID

        // Save the player under the generated key within the team ID
        database.child("players").child(teamId).child(playerKey).setValue(newPlayer)
            .addOnSuccessListener {
                Toast.makeText(this, "Player added successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to add player: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
