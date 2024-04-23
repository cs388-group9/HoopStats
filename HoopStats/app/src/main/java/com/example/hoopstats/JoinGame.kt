package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class JoinGame : AppCompatActivity() {

    private lateinit var gameIdEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        gameIdEditText = findViewById(R.id.gameIdEditText)
        submitButton = findViewById(R.id.submitButton)
        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Add a click listener to the return button
        findViewById<Button>(R.id.returnButton).setOnClickListener {
            // Finish the current activity and return to the previous activity (GamesMain)
            finish()
        }

        submitButton.setOnClickListener {
            val gameId = gameIdEditText.text.toString()
            if (gameId.isNotEmpty()) {
                searchGameInDatabase(gameId)
            } else {
                Toast.makeText(this, "Please enter a game ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchGameInDatabase(gameId: String) {
        databaseReference.child("games").child(gameId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Game found, navigate to GamesMain with game data
                    val intent = Intent(this@JoinGame, GamesMain::class.java)
                    intent.putExtra("gameId", gameId)
                    startActivity(intent)
                    finish()
                } else {
                    // Game not found
                    Toast.makeText(this@JoinGame, "Game not found, try again", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@JoinGame, "Error searching for game: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
