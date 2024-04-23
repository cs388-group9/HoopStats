package com.example.hoopstats

import android.content.Intent
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

        // Set up RecyclerViews
        teamARecyclerView.layoutManager = LinearLayoutManager(this)
        teamBRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize RecyclerView adapters
        playerAdapterA = PlayerAdapter(this, teamAPlayers)
        playerAdapterB = PlayerAdapter(this, teamBPlayers)

        teamARecyclerView.adapter = playerAdapterA
        teamBRecyclerView.adapter = playerAdapterB

        // Populate players
        populatePlayers()

        // Set click listener to open IncrementStatsActivity when a player is clicked
        playerAdapterA.setOnItemClickListener { player ->
            openIncrementStatsActivity(player)
        }

        playerAdapterB.setOnItemClickListener { player ->
            openIncrementStatsActivity(player)
        }
    }

    private fun openIncrementStatsActivity(player: Player) {
        val intent = Intent(this, IncrementStatsActivity::class.java)
        intent.putExtra("player", player)
        startActivityForResult(intent, REQUEST_CODE_INCREMENT_STATS)
    }

    private fun populatePlayers() {
        // Dummy data for testing, replace with actual data retrieval
        teamAPlayers.add(Player("Player A1", 1, 5, 3, 2, 1, 4))
        teamAPlayers.add(Player("Player A2", 1, 7, 2, 1, 0, 2))
        teamBPlayers.add(Player("Player B1", 2, 4, 6, 3, 2, 1))
        teamBPlayers.add(Player("Player B2", 2, 6, 4, 2, 1, 3))

        // Log player data
        Log.d("TrackStats", "Team A Players:")
        for (player in teamAPlayers) {
            Log.d("TrackStats", "Player Name: ${player.playerName}, Team: ${player.team}, PTS: ${player.totalPoints}, RBS: ${player.rebounds}, AST: ${player.assists}")
        }

        Log.d("TrackStats", "Team B Players:")
        for (player in teamBPlayers) {
            Log.d("TrackStats", "Player Name: ${player.playerName}, Team: ${player.team}, PTS: ${player.totalPoints}, RBS: ${player.rebounds}, AST: ${player.assists}")
        }

        // Notify adapters of data set changes
        playerAdapterA.notifyDataSetChanged()
        playerAdapterB.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INCREMENT_STATS && resultCode == RESULT_OK && data != null) {
            val updatedPlayer = data.getSerializableExtra("updatedPlayer") as? Player
            if (updatedPlayer != null) {
                // Update the player's stats in the list and notify adapter
                updatePlayerStats(updatedPlayer)
                Log.d("TrackStats", "Received updated player stats: $updatedPlayer")
            }
        }
    }

    private fun updatePlayerStats(updatedPlayer: Player) {
        val index = when (updatedPlayer.team) {
            1 -> teamAPlayers.indexOfFirst { it.playerName == updatedPlayer.playerName }
            2 -> teamBPlayers.indexOfFirst { it.playerName == updatedPlayer.playerName }
            else -> -1
        }
        if (index != -1) {
            // Replace the player in the respective list with the updated player
            when (updatedPlayer.team) {
                1 -> teamAPlayers[index] = updatedPlayer
                2 -> teamBPlayers[index] = updatedPlayer
            }
            // Notify adapter of data set change
            playerAdapterA.notifyItemChanged(index) // Update only the changed item
            playerAdapterB.notifyItemChanged(index) // Update only the changed item
        }
    }



    companion object {
        private const val REQUEST_CODE_INCREMENT_STATS = 1001
    }
}
