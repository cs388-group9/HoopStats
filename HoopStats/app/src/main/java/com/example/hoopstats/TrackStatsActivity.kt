package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
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

        gameId = intent.getStringExtra("gameId") ?: ""

        initializeViews()
        setupRecyclerViews()
        fetchGameData(gameId)
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

        // Setup the create player button
        val createPlayerButton = findViewById<Button>(R.id.createPlayerButton)
        createPlayerButton.setOnClickListener {
            // Make sure team IDs are retrieved and stored before attempting to pass them to the CreatePlayerActivity
            if(team1Id != null && team2Id != null) {
                val intent = Intent(this, CreatePlayerActivity::class.java).apply {
                    putExtra("team1Name", team1NameTextView.text.toString())
                    putExtra("team2Name", team2NameTextView.text.toString())
                    putExtra("team1Id", team1Id)
                    putExtra("team2Id", team2Id)
                }
                startActivity(intent)
            }
        }
    }
    private fun setupRecyclerViews() {
        playerAdapterA = PlayerAdapter(teamAPlayers)
        teamARecyclerView.adapter = playerAdapterA
        teamARecyclerView.layoutManager = LinearLayoutManager(this)

        playerAdapterB = PlayerAdapter(teamBPlayers)
        teamBRecyclerView.adapter = playerAdapterB
        teamBRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchGameData(gameId: String) {
        // Fetch game data, including the team IDs
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
}
