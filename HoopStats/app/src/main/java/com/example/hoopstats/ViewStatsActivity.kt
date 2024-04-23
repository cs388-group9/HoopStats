package com.example.hoopstats

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.models.Team
import com.google.firebase.database.*

class ViewStatsActivity : AppCompatActivity() {

    private lateinit var gameTitleTextView: TextView
    private lateinit var team1NameTextView: TextView
    private lateinit var team2NameTextView: TextView
    private lateinit var team1ReboundsTextView: TextView
    private lateinit var team1AssistsTextView: TextView
    private lateinit var team1TwoPointersTextView: TextView
    private lateinit var team1ThreePointersTextView: TextView
    private lateinit var team1FreeThrowsTextView: TextView
    private lateinit var team1TotalPointsTextView: TextView

    private lateinit var team2ReboundsTextView: TextView
    private lateinit var team2AssistsTextView: TextView
    private lateinit var team2TwoPointersTextView: TextView
    private lateinit var team2ThreePointersTextView: TextView
    private lateinit var team2FreeThrowsTextView: TextView
    private lateinit var team2TotalPointsTextView: TextView

    private var gameId: String? = null
    private var team1Id: String? = null
    private var team2Id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        initializeViews()
        retrieveIntentData()
        fetchTeamStats()
        val trackStatsButton = findViewById<Button>(R.id.trackStatsButton)
        trackStatsButton.setOnClickListener {
            finish() // Close the current activity (ViewStatsActivity)
        }
    }

    private fun initializeViews() {
        gameTitleTextView = findViewById(R.id.gameTitleTextView)
        team1NameTextView = findViewById(R.id.team1NameTextView)
        team2NameTextView = findViewById(R.id.team2NameTextView)

        team1ReboundsTextView = findViewById(R.id.team1ReboundsTextView)
        team1AssistsTextView = findViewById(R.id.team1AssistsTextView)
        team1TwoPointersTextView = findViewById(R.id.team1TwoPointersTextView)
        team1ThreePointersTextView = findViewById(R.id.team1ThreePointersTextView)
        team1FreeThrowsTextView = findViewById(R.id.team1FreeThrowsTextView)
        team1TotalPointsTextView = findViewById(R.id.team1TotalPointsTextView)

        team2ReboundsTextView = findViewById(R.id.team2ReboundsTextView)
        team2AssistsTextView = findViewById(R.id.team2AssistsTextView)
        team2TwoPointersTextView = findViewById(R.id.team2TwoPointersTextView)
        team2ThreePointersTextView = findViewById(R.id.team2ThreePointersTextView)
        team2FreeThrowsTextView = findViewById(R.id.team2FreeThrowsTextView)
        team2TotalPointsTextView = findViewById(R.id.team2TotalPointsTextView)
    }

    private fun retrieveIntentData() {
        gameId = intent.getStringExtra("gameId")
        team1Id = intent.getStringExtra("team1Id")
        team2Id = intent.getStringExtra("team2Id")
        Log.e("!!!!!!!!!!!", gameId.toString())
        Log.e("!!!!!!!!!!!", team1Id.toString())
        Log.e("!!!!!!!!!!!", team2Id.toString())

        val gameName = intent.getStringExtra("gameName")
        gameTitleTextView.text = gameName ?: "Game Details"

        team1NameTextView.text = intent.getStringExtra("team1Name") ?: "Team 1"
        team2NameTextView.text = intent.getStringExtra("team2Name") ?: "Team 2"
    }

    private fun fetchTeamStats() {
        team1Id?.let {
            fetchTeamData(it, true)
        }
        team2Id?.let {
            fetchTeamData(it, false)
        }
    }

    private fun fetchTeamData(teamId: String, isTeam1: Boolean) {
        val teamRef = FirebaseDatabase.getInstance().getReference("teams/$teamId")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Team::class.java)?.let { team ->
                    if (isTeam1) {
                        updateTeamStatsView(team, true)
                    } else {
                        updateTeamStatsView(team, false)
                    }
                } ?: Log.e("ViewStatsActivity", "No team data found for teamId: $teamId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewStatsActivity", "Error fetching team data for teamId: $teamId", error.toException())
            }
        })
    }

    private fun updateTeamStatsView(team: Team, isTeam1: Boolean) {
        val totalPoints = team.twoPointers * 2 + team.threePointers * 3 + team.freeThrows

        if (isTeam1) {
            team1ReboundsTextView.text = team.rebounds.toString()
            team1AssistsTextView.text = team.assists.toString()
            team1TwoPointersTextView.text = team.twoPointers.toString()
            team1ThreePointersTextView.text = team.threePointers.toString()
            team1FreeThrowsTextView.text = team.freeThrows.toString()
            team1TotalPointsTextView.text = totalPoints.toString()
        } else {
            team2ReboundsTextView.text = team.rebounds.toString()
            team2AssistsTextView.text = team.assists.toString()
            team2TwoPointersTextView.text = team.twoPointers.toString()
            team2ThreePointersTextView.text = team.threePointers.toString()
            team2FreeThrowsTextView.text = team.freeThrows.toString()
            team2TotalPointsTextView.text = totalPoints.toString()
        }
    }

}
