package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GamesMain : AppCompatActivity() {

    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var noGamesTextView: TextView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var gamesList: MutableList<Game> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_main)

        gamesRecyclerView = findViewById(R.id.gamesRecyclerView)

        noGamesTextView = findViewById(R.id.noGamesTextView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter(gamesList)
        gamesRecyclerView.adapter = gameAdapter

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = initializeGoogleSignIn()

        findViewById<Button>(R.id.createGameButton).setOnClickListener {
            startActivityForResult(Intent(this, CreateGame::class.java), 1001)
        }

        findViewById<Button>(R.id.logout_button).setOnClickListener {
            signOutAndStartSignInActivity()
        }

        findViewById<Button>(R.id.joinGameButton).setOnClickListener {
            startActivity(Intent(this, JoinGame::class.java))
        }

        fetchGames() // Fetch games from Firebase
    }

    private fun initializeGoogleSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun fetchGames() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("GamesMain", "User not logged in")
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("games")
        databaseReference.orderByChild("creatorUserId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    gamesList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val game = snapshot.getValue(Game::class.java)
                        if (game != null) {
                            gamesList.add(game)
                        } else {
                            Log.e("GamesMain", "Error parsing game data: ${snapshot.key}")
                        }
                    }
                    gameAdapter.notifyDataSetChanged()
                    updateUI()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("GamesMain", "Database error: ${databaseError.message}")
                }
            })
    }

    private fun updateUI() {
        if (gamesList.isEmpty()) {
            noGamesTextView.visibility = View.VISIBLE
            gamesRecyclerView.visibility = View.GONE
        } else {
            noGamesTextView.visibility = View.GONE
            gamesRecyclerView.visibility = View.VISIBLE
        }
        gameAdapter.notifyDataSetChanged()
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
