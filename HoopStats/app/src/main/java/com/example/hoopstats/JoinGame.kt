package com.example.hoopstats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class JoinGame : AppCompatActivity() {

    private lateinit var gameIdEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        gameIdEditText = findViewById(R.id.gameIdEditText)
        submitButton = findViewById(R.id.submitButton)

        database = FirebaseDatabase.getInstance().reference

        submitButton.setOnClickListener {
            val gameId = gameIdEditText.text.toString().trim()
            if (gameId.isNotEmpty()) {
                joinGame(gameId)
            } else {
                Toast.makeText(this, "Please enter a Game ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun joinGame(gameId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Fetch game data from Firebase
        database.child("games").child(gameId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val game = dataSnapshot.getValue(Game::class.java)
                if (game != null) {
                    // Modify the game data to add the user's ID to submittedByUserIds
                    val submittedByUserIds = game.submittedByUserIds.toMutableList()
                    submittedByUserIds.add(userId)

                    // Update the game data in Firebase
                    val updates = hashMapOf<String, Any>(
                        "/games/$gameId/submittedByUserIds" to submittedByUserIds
                    )
                    database.updateChildren(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this@JoinGame, "Joined game successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@JoinGame, "Failed to join game. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this@JoinGame, "Game not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@JoinGame, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
