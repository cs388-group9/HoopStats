package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.example.hoopstats.models.Player
import com.google.firebase.database.*

class TrackStatsActivity : AppCompatActivity() {

    private lateinit var teamARecyclerView: RecyclerView
    private lateinit var teamBRecyclerView: RecyclerView
    private lateinit var gameTitleTextView: TextView
    private lateinit var team1NameTextView: TextView
    private lateinit var team2NameTextView: TextView
    private lateinit var playerAdapterA: PlayerAdapter
    private lateinit var playerAdapterB: PlayerAdapter
    private var teamAPlayers: MutableList<Player> = mutableListOf()
    private var teamBPlayers: MutableList<Player> = mutableListOf()
    private lateinit var gameId: String
    private var team1Id: String? = null
    private var team2Id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_stats)
        Log.d("TrackStatsActivity", "onCreate: Started")

        gameId = intent.getStringExtra("gameId") ?: ""

        initializeViews()
        setupRecyclerViews()
        fetchGameData(gameId)

        val viewStatsButton = findViewById<Button>(R.id.viewStatsButton)
        viewStatsButton.setOnClickListener {
            // Create an intent to navigate to ViewStatsActivity
// Inside the viewStatsButton.setOnClickListener
            val intent = Intent(this, ViewStatsActivity::class.java).apply {
                putExtra("gameId", gameId)
                putExtra("gameName", gameTitleTextView.text.toString())
                putExtra("teamAPlayers", teamAPlayers.toTypedArray())
                putExtra("teamBPlayers", teamBPlayers.toTypedArray())
                putExtra("team1Name", team1NameTextView.text.toString())
                putExtra("team2Name", team2NameTextView.text.toString())
            }
            startActivity(intent)

        }
    }


    private fun initializeViews() {
        // Initialize views
        teamARecyclerView = findViewById(R.id.team1RecyclerView)
        teamBRecyclerView = findViewById(R.id.team2RecyclerView)
        gameTitleTextView = findViewById(R.id.gameTitleTextView)
        team1NameTextView = findViewById(R.id.team1NameTextView)
        team2NameTextView = findViewById(R.id.team2NameTextView)

        // Setup RecyclerViews
        playerAdapterA = PlayerAdapter(teamAPlayers)
        teamARecyclerView.adapter = playerAdapterA
        teamARecyclerView.layoutManager = LinearLayoutManager(this)

        playerAdapterB = PlayerAdapter(teamBPlayers)
        teamBRecyclerView.adapter = playerAdapterB
        teamBRecyclerView.layoutManager = LinearLayoutManager(this)
// Mock players for team A with random stats
        val mockPlayerA1 = Player("Player A1", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())
        val mockPlayerA2 = Player("Player A2", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())
        val mockPlayerA3 = Player("Player A3", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())

// Mock players for team B with random stats
        val mockPlayerB1 = Player("Player B1", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())
        val mockPlayerB2 = Player("Player B2", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())
        val mockPlayerB3 = Player("Player B3", (0..10).random(), (0..10).random(), (0..5).random(), (0..3).random(), (0..5).random())

        teamAPlayers.addAll(listOf(mockPlayerA1, mockPlayerA2, mockPlayerA3))
        teamBPlayers.addAll(listOf(mockPlayerB1, mockPlayerB2, mockPlayerB3))

        // Setup the create player button
        val createPlayerButton = findViewById<Button>(R.id.createPlayerButton)
        createPlayerButton.setOnClickListener {
            // Make sure team IDs are retrieved and stored before attempting to pass them to the CreatePlayerActivity
            if(team1Id != null && team2Id != null) {
                Log.d("TrackStatsActivity", "Team 1 ID: $team1Id")
                Log.d("TrackStatsActivity", "Team 2 ID: $team2Id")

                val intent = Intent(this, CreatePlayerActivity::class.java).apply {
                    putExtra("team1Name", team1NameTextView.text.toString())
                    putExtra("team2Name", team2NameTextView.text.toString())
                    putExtra("team1Id", team1Id)
                    putExtra("team2Id", team2Id)
                }
                startActivity(intent)
            } else {
                Log.e("TrackStatsActivity", "Team IDs are null.")
            }
        }
    }

    private fun setupRecyclerViews() {
        // Already initialized in initializeViews()
    }

    private fun fetchGameData(gameId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("games/$gameId")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val game = dataSnapshot.getValue(Game::class.java)
                gameTitleTextView.text = game?.gameName ?: "Unknown Game"
                game?.teamIds?.let { teamIds ->
                    if (teamIds.size >= 2) {
                        team1Id = teamIds[0]
                        team2Id = teamIds[1]
                        fetchTeamName(team1Id!!, team1NameTextView, playerAdapterA, teamAPlayers)
                        fetchTeamName(team2Id!!, team2NameTextView, playerAdapterB, teamBPlayers)
                        fetchTeamPlayers(team1Id!!, teamAPlayers, playerAdapterA)
                        fetchTeamPlayers(team2Id!!, teamBPlayers, playerAdapterB)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun fetchTeamName(teamId: String, teamNameTextView: TextView, adapter: PlayerAdapter, playerList: MutableList<Player>) {
        // Fetch team name and players
        val teamRef = FirebaseDatabase.getInstance().getReference("teams/$teamId")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teamName = dataSnapshot.child("teamName").getValue(String::class.java)
                teamName?.let {
                    teamNameTextView.text = it
                }
                dataSnapshot.child("players").children.forEach { childSnapshot ->
                    val player = childSnapshot.getValue(Player::class.java)
                    player?.let { playerList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
    private fun fetchTeamPlayers(teamId: String, playerList: MutableList<Player>, adapter: PlayerAdapter) {
        val teamPlayersRef = FirebaseDatabase.getInstance().getReference("teams/$teamId/players")
        teamPlayersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (playerSnapshot in dataSnapshot.children) {
                    val player = playerSnapshot.getValue(Player::class.java)
                    player?.let { playerList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TrackStatsActivity", "Failed to fetch team players: ${databaseError.message}")
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INCREMENT_STATS && resultCode == RESULT_OK && data != null) {
            val updatedPlayer = data.getSerializableExtra("updatedPlayer") as? Player
            if (updatedPlayer != null) {
                updatePlayerStats(updatedPlayer)
                Log.d("TrackStats", "Received updated player stats: $updatedPlayer")
            }
        }
    }

    private fun updatePlayerStats(updatedPlayer: Player) {
        val indexA = teamAPlayers.indexOfFirst { it.playerName == updatedPlayer.playerName }
        val indexB = teamBPlayers.indexOfFirst { it.playerName == updatedPlayer.playerName }

        if (indexA != -1) {
            teamAPlayers[indexA] = updatedPlayer
            playerAdapterA.notifyItemChanged(indexA)
        }

        if (indexB != -1) {
            teamBPlayers[indexB] = updatedPlayer
            playerAdapterB.notifyItemChanged(indexB)
        }
    }



    companion object {
        private const val REQUEST_CODE_INCREMENT_STATS = 1001
    }
}
