package com.example.hoopstats

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.R
import android.widget.Spinner
class CreateGame : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        val submitButton: Button = findViewById(R.id.submitButton)
        val gameNameEditText: EditText = findViewById(R.id.gameNameEditText)
        val playersSpinner: Spinner = findViewById(R.id.playersSpinner)
        val teamName1EditText: EditText = findViewById(R.id.teamName1EditText)
        val teamName2EditText: EditText = findViewById(R.id.teamName2EditText)

        submitButton.setOnClickListener {
            val gameName = gameNameEditText.text.toString()
            val numberOfPlayers = playersSpinner.selectedItem.toString()
            val team1Name = teamName1EditText.text.toString()
            val team2Name = teamName2EditText.text.toString()

            if (gameName.isNotEmpty() && numberOfPlayers.isNotEmpty() && team1Name.isNotEmpty() && team2Name.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("gameName", gameName)
                // Convert numberOfPlayers to integer if needed
                // val numberOfPlayersInt = numberOfPlayers.toInt()
                resultIntent.putExtra("numberOfPlayers", numberOfPlayers)
                resultIntent.putExtra("team1Name", team1Name)
                resultIntent.putExtra("team2Name", team2Name)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                // Show a message or toast indicating that all fields are required
                // For example:
                // Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
