package com.example.hoopstats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CreateGame : AppCompatActivity() {

    private lateinit var gameNameEditText: EditText
    private lateinit var playersSpinner: Spinner
    private lateinit var teamName1EditText: EditText
    private lateinit var teamName2EditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        // Initialize views
        gameNameEditText = findViewById(R.id.gameNameEditText)
        playersSpinner = findViewById(R.id.playersSpinner)
        teamName1EditText = findViewById(R.id.teamName1EditText)
        teamName2EditText = findViewById(R.id.teamName2EditText)
        submitButton = findViewById(R.id.submitButton)

        // Set click listener for submit button
        submitButton.setOnClickListener {
            createGame()
        }
    }

    private fun createGame() {
        val gameName = gameNameEditText.text.toString()
        val numberOfPlayers = playersSpinner.selectedItem.toString().toInt()
        val teamName1 = teamName1EditText.text.toString()
        val teamName2 = teamName2EditText.text.toString()

        // Perform the game creation logic here
        // For demonstration, let's just log the details
        println("Game Name: $gameName")
        println("Number of Players: $numberOfPlayers")
        println("Team Name 1: $teamName1")
        println("Team Name 2: $teamName2")

        // You can add your game creation logic here, such as saving to a database
        // or sending the details to a server
    }
}
