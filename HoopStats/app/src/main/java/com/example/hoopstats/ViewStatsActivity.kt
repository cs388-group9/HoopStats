package com.example.hoopstats

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.models.Player

class ViewStatsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        // Retrieve data from intent extras
        val gameId = intent.getStringExtra("gameId") ?: ""
        val teamAPlayers = intent.getSerializableExtra("teamAPlayers") as Array<Player>
        val teamBPlayers = intent.getSerializableExtra("teamBPlayers") as Array<Player>

        // Initialize views
        val gameTitleTextView = findViewById<TextView>(R.id.gameTitleTextView)
        val team1NameTextView = findViewById<TextView>(R.id.team1NameTextView)
        val team2NameTextView = findViewById<TextView>(R.id.team2NameTextView)

        // Set game title
        gameTitleTextView.text = intent.getStringExtra("gameName") ?: ""

        // Set team names
// Inside onCreate method
        val team1Name = intent.getStringExtra("team1Name") ?: ""
        val team2Name = intent.getStringExtra("team2Name") ?: ""

// Set team names
        team1NameTextView.text = team1Name
        team2NameTextView.text = team2Name

        // Initialize TrackStatsButton
        val trackStatsButton = findViewById<Button>(R.id.trackStatsButton)
        trackStatsButton.setOnClickListener {
            // Navigate back to TrackStatsActivity
            onBackPressed()
        }

        // Update team stats
        updateTeamStats(teamAPlayers, teamBPlayers)
    }

    private fun updateTeamStats(teamAPlayers: Array<Player>, teamBPlayers: Array<Player>) {
        // Update team A stats
        findViewById<TextView>(R.id.team1ReboundsTextView).text = calculateTotal(teamAPlayers) { it.rebounds }.toString()
        findViewById<TextView>(R.id.team1AssistsTextView).text = calculateTotal(teamAPlayers) { it.assists }.toString()
        findViewById<TextView>(R.id.team1TwoPointersTextView).text = calculateTotal(teamAPlayers) { it.twoPointers }.toString()
        findViewById<TextView>(R.id.team1ThreePointersTextView).text = calculateTotal(teamAPlayers) { it.threePointers }.toString()
        findViewById<TextView>(R.id.team1FreeThrowsTextView).text = calculateTotal(teamAPlayers) { it.freeThrows }.toString()

        // Update team B stats
        findViewById<TextView>(R.id.team2ReboundsTextView).text = calculateTotal(teamBPlayers) { it.rebounds }.toString()
        findViewById<TextView>(R.id.team2AssistsTextView).text = calculateTotal(teamBPlayers) { it.assists }.toString()
        findViewById<TextView>(R.id.team2TwoPointersTextView).text = calculateTotal(teamBPlayers) { it.twoPointers }.toString()
        findViewById<TextView>(R.id.team2ThreePointersTextView).text = calculateTotal(teamBPlayers) { it.threePointers }.toString()
        findViewById<TextView>(R.id.team2FreeThrowsTextView).text = calculateTotal(teamBPlayers) { it.freeThrows }.toString()

        val team1TotalPoints = calculateTotalPoints(teamAPlayers)
        val team2TotalPoints = calculateTotalPoints(teamBPlayers)

        findViewById<TextView>(R.id.team1TotalPointsTextView).text = team1TotalPoints.toString()
        findViewById<TextView>(R.id.team2TotalPointsTextView).text = team2TotalPoints.toString()

    }

    private fun calculateTotalPoints(players: Array<Player>): Int {
        var totalPoints = 0
        for (player in players) {
            totalPoints += player.twoPointers * 2 + player.threePointers * 3 + player.freeThrows
        }
        return totalPoints
    }
    private fun calculateTotal(players: Array<Player>, selector: (Player) -> Int): Int {
        var total = 0
        for (player in players) {
            total += selector(player)
        }
        return total
    }
}
