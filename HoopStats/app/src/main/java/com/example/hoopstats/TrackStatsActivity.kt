package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.example.hoopstats.models.Player
import com.google.firebase.auth.FirebaseAuth
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
    private var team1Id: String? = null
    private var team2Id: String? = null
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_stats)

        initializeViews()
        setupRecyclerViews()

        val gameId = intent.getStringExtra("gameId") ?: ""
        fetchGameData(gameId)
        val viewStatsButton = findViewById<Button>(R.id.viewStatsButton)
        viewStatsButton.setOnClickListener {
            val intent = Intent(this, ViewStatsActivity::class.java).apply {
                putExtra("gameId", gameId)
                putExtra("gameName", gameTitleTextView.text.toString())
                putExtra("teamAPlayers", teamAPlayers.toTypedArray())
                putExtra("teamBPlayers", teamBPlayers.toTypedArray())
                putExtra("team1Name", team1NameTextView.text.toString())
                putExtra("team2Name", team2NameTextView.text.toString())
                putExtra("team1Id", team1Id)
                putExtra("team2Id", team2Id)
            }
            startActivity(intent)
        }
    }

    private fun initializeViews() {
        teamARecyclerView = findViewById(R.id.team1RecyclerView)
        teamBRecyclerView = findViewById(R.id.team2RecyclerView)
        gameTitleTextView = findViewById(R.id.gameTitleTextView)
        team1NameTextView = findViewById(R.id.team1NameTextView)
        team2NameTextView = findViewById(R.id.team2NameTextView)
        val createPlayerButton = findViewById<Button>(R.id.createPlayerButton)

        // Check if the current user's ID is in the array of submittedByUserIds
        val userInArray = userId != null && intent.getStringArrayExtra("submittedByUserIds")?.contains(userId) == true
        Log.e("!!!!!!!!!!!!!!!!!!", intent.getStringArrayExtra("submittedByUserIds").toString())

        // Hide and disable the "Create a Player" button if the user's ID is in the array
        if (userInArray) {
            createPlayerButton.visibility = View.GONE
            createPlayerButton.isEnabled = false
        } else {
            createPlayerButton.setOnClickListener {
                if (team1Id != null && team2Id != null) {
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
    }





    private fun setupRecyclerViews() {
        playerAdapterA = PlayerAdapter()
        playerAdapterB = PlayerAdapter()

        teamARecyclerView.apply {
            adapter = playerAdapterA
            layoutManager = LinearLayoutManager(this@TrackStatsActivity)
        }

        teamBRecyclerView.apply {
            adapter = playerAdapterB
            layoutManager = LinearLayoutManager(this@TrackStatsActivity)
        }
    }

    private fun fetchTeamName(teamId: String, textView: TextView) {
        val teamRef = FirebaseDatabase.getInstance().getReference("teams/$teamId/teamName")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teamName = dataSnapshot.getValue(String::class.java)
                textView.text = teamName ?: "Unknown Team"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TrackStatsActivity", "Failed to fetch team name for team $teamId: ${databaseError.message}")
            }
        })
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
                        fetchTeamName(team1Id!!, team1NameTextView)
                        fetchTeamName(team2Id!!, team2NameTextView)
                        fetchPlayers(team1Id!!, playerAdapterA)
                        fetchPlayers(team2Id!!, playerAdapterB)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TrackStatsActivity", "Failed to fetch game data: ${databaseError.message}")
            }
        })
    }

    private fun fetchPlayers(teamId: String, adapter: PlayerAdapter) {
        val playersRef = FirebaseDatabase.getInstance().getReference("players/$teamId")
        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val players = mutableListOf<Player>()
                for (playerSnapshot in dataSnapshot.children) {
                    val player = playerSnapshot.getValue(Player::class.java)
                    player?.let { players.add(it) }
                }
                if (players.isNotEmpty()) {
                    adapter.updatePlayers(players)
                    val totalPoints = calculateTotalPoints(players.toTypedArray())
                    updateTeamTotalPoints(teamId, totalPoints)
                } else {
                    Log.d("TrackStatsActivity", "No players found for team $teamId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TrackStatsActivity", "Failed to fetch players for team $teamId: ${databaseError.message}")
            }
        })
    }

    private fun updateTeamTotalPoints(teamId: String, totalPoints: Int) {
        if (teamId == team1Id) {
            findViewById<TextView>(R.id.team1TotalPoints).text = totalPoints.toString()
        } else if (teamId == team2Id) {
            findViewById<TextView>(R.id.team2TotalPoints).text = totalPoints.toString()
        }
    }

    private fun calculateTotalPoints(players: Array<Player>): Int {
        var totalPoints = 0
        for (player in players) {
            totalPoints += player.twoPointers * 2 + player.threePointers * 3 + player.freeThrows
        }
        return totalPoints
    }
}
