package com.example.hoopstats
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.Game
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

    // Request code for starting CreateGame activity
    private val CREATE_GAME_REQUEST_CODE = 1001

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


      //  gamesList.addAll(getGames()) // Fetch games


        updateUI()

        // Set up click listener for the create game button
        findViewById<View>(R.id.createGameButton).setOnClickListener {
            startActivityForResult(Intent(this, CreateGame::class.java), CREATE_GAME_REQUEST_CODE)
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

    // Handle the result from CreateGame activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_GAME_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let { gameData ->
                val gameName = gameData.getStringExtra("gameName")
                val numberOfPlayersStr = gameData.getStringExtra("numberOfPlayers")
                val team1Name = gameData.getStringExtra("team1Name")
                val team2Name = gameData.getStringExtra("team2Name")

                // Ensure numberOfPlayersStr is not null or empty before converting to Int
                val numberOfPlayers = numberOfPlayersStr?.toIntOrNull()

                if (gameName != null && numberOfPlayers != null && team1Name != null && team2Name != null) {
                    // Find the maximum game ID in the current list of games
                    val maxGameId = gamesList.maxByOrNull { it.gameId }?.gameId ?: 10000

                    // Increment the maximum game ID by one to get the new game ID
                    val newGameId = maxGameId + 1

                    // Log the new game data
                    Log.d("GamesMain", "Received new game data: $gameName, $numberOfPlayers, $team1Name, $team2Name")

                    // Create a new game with the incremented game ID
                    val newGame = Game(newGameId, gameName, numberOfPlayers, team1Name, team2Name)
                    gamesList.add(newGame)
                    updateUI()
                } else {
                    Log.e("GamesMain", "Error parsing game data")
                }
            }
        }
    }


    // Update the UI based on the games list
    private fun updateUI() {
        if (gamesList.isEmpty()) {
            noGamesTextView.visibility = View.VISIBLE
            noGamesTextView.text = "No Games. Create or Join one!"
        } else {
            noGamesTextView.visibility = View.GONE
            gameAdapter.notifyDataSetChanged()
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

    // Function to get the initial list of games

    //testing games
    /*
    private fun getGames(): List<Game> {
        val games = mutableListOf<Game>()
        games.add(Game(10001, "Game 1", 5, "Team A", "Team B"))
        games.add(Game(10002, "Game 2", 8, "Team C", "Team D"))
        return games
    }


     */
}
