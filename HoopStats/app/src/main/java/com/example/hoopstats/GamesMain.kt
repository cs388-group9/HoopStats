package com.example.hoopstats
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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

        // Initialize RecyclerView and adapter
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView)
        gameAdapter = GameAdapter(gamesList)
        gamesRecyclerView.adapter = gameAdapter
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize noGamesTextView
        noGamesTextView = findViewById(R.id.noGamesTextView)

        // Load games data or display message
        if (gamesList.isEmpty()) {
            noGamesTextView.visibility = View.VISIBLE
            noGamesTextView.text = "No Games. Create or Join one!"
        } else {
            noGamesTextView.visibility = View.GONE
            // If games exist, add them to the RecyclerView
            gamesList.addAll(getGames()) // Replace getGames() with your function to fetch games
            gameAdapter.notifyDataSetChanged()
        }
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val sign_out_button = findViewById<Button>(R.id.logout_button)
        sign_out_button.setOnClickListener {
            signOutAndStartSignInActivity()
        }
    }



private fun signOutAndStartSignInActivity() {

    mAuth.signOut()

    mGoogleSignInClient.signOut().addOnCompleteListener(this) {
        // Optional: Update UI or show a message to the user
        val intent = Intent(this@GamesMain, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}


    // Function to fetch games (replace this with your actual implementation)
    private fun getGames(): List<Game> {
        // Return a list of games from your data source
        return listOf(
            Game("Game 1", 5, "Team A", "Team B"),
            Game("Game 2", 8, "Team C", "Team D")
            // Add more games if needed
        )
    }
}
