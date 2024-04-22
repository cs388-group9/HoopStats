package com.example.hoopstats

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Player
class TrackStats : AppCompatActivity() {

    private lateinit var teamARecyclerView: RecyclerView
    private lateinit var teamBRecyclerView: RecyclerView
    private lateinit var gameTitleTextView: TextView
    private lateinit var team1NameTextView: TextView
    private lateinit var team2NameTextView: TextView
    private lateinit var playerAdapterA: PlayerAdapter
    private lateinit var playerAdapterB: PlayerAdapter
    private var teamAPlayers: MutableList<Player> = mutableListOf()
    private var teamBPlayers: MutableList<Player> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_stats)

        // Initialize views
        teamARecyclerView = findViewById(R.id.team1RecyclerView)
        teamBRecyclerView = findViewById(R.id.team2RecyclerView)
        gameTitleTextView = findViewById(R.id.gameTitleTextView)
        team1NameTextView = findViewById(R.id.team1NameTextView)
        team2NameTextView = findViewById(R.id.team2NameTextView)

        // Set up RecyclerViews
        playerAdapterA = PlayerAdapter(teamAPlayers)
        playerAdapterB = PlayerAdapter(teamBPlayers)
        teamARecyclerView.adapter = playerAdapterA
        teamBRecyclerView.adapter = playerAdapterB
        teamARecyclerView.layoutManager = LinearLayoutManager(this)
        teamBRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get data from intent
        val gameId = intent.getStringExtra("gameId")
        val gameName = intent.getStringExtra("gameName")
        val team1Name = intent.getStringExtra("team1Name")
        val team2Name = intent.getStringExtra("team2Name")

        // Log the received game information
        Log.d("TrackStats", "Game ID: $gameId")
        Log.d("TrackStats", "Game Title: $gameName")
        Log.d("TrackStats", "Team 1 Name: $team1Name")
        Log.d("TrackStats", "Team 2 Name: $team2Name")

        // Set data to views
        gameTitleTextView.text = gameName
        team1NameTextView.text = team1Name
        team2NameTextView.text = team2Name

        // Populate players (replace with actual data)
        populatePlayers()
    }

    private fun populatePlayers() {
        // Dummy data for testing, replace with actual data retrieval
        teamAPlayers.add(Player("Player A1", 1, 5, 3, 2, 1, 4))
        teamAPlayers.add(Player("Player A2", 1, 7, 2, 1, 0, 2))
        teamBPlayers.add(Player("Player B1", 2, 4, 6, 3, 2, 1))
        teamBPlayers.add(Player("Player B2", 2, 6, 4, 2, 1, 3))

        // Notify adapters of data set changes
        playerAdapterA.notifyDataSetChanged()
        playerAdapterB.notifyDataSetChanged()
    }
}
