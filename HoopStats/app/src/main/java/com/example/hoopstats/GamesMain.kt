package com.example.hoopstats
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GamesMain : AppCompatActivity() {

    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var noGamesTextView: TextView
    private lateinit var gameAdapter: GameAdapter
    private var gamesList: MutableList<Game> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_main)

        // Initialize RecyclerView and adapter
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView)
        gameAdapter = GameAdapter(gamesList)
        gamesRecyclerView.adapter = gameAdapter
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

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
