package com.example.hoopstats

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.example.hoopstats.models.*

class IncrementStatsActivity : AppCompatActivity() {

    private lateinit var reboundsValueTextView: TextView
    private lateinit var assistsValueTextView: TextView
    private lateinit var twoPointersValueTextView: TextView
    private lateinit var threePointersValueTextView: TextView
    private lateinit var freeThrowsValueTextView: TextView
    private lateinit var reboundsIncrementButton: Button
    private lateinit var assistsIncrementButton: Button
    private lateinit var twoPointersIncrementButton: Button
    private lateinit var threePointersIncrementButton: Button
    private lateinit var freeThrowsIncrementButton: Button
    private lateinit var reboundsDecrementButton: Button
    private lateinit var assistsDecrementButton: Button
    private lateinit var twoPointersDecrementButton: Button
    private lateinit var threePointersDecrementButton: Button
    private lateinit var freeThrowsDecrementButton: Button
    private lateinit var submitButton: Button
    private var teamId: String = ""
    private var playerId: String = ""
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_increment_stats)

        player = intent.getSerializableExtra("player") as Player
        playerId = player.playerId
        teamId = player.teamId
        Log.e("!!!!!!!!!!!!!!!!!!!", playerId)

        initializeViews()
        setupListeners()

        fetchPlayerStats()
        submitButton.setOnClickListener {
            val oldPlayer = player.copy() // Assuming 'player' is the current player stats before updates
            updateStatsInDatabase()
            val newPlayer = Player(
                playerName = player.playerName,
                teamId = player.teamId,
                rebounds = reboundsValueTextView.text.toString().toInt(),
                assists = assistsValueTextView.text.toString().toInt(),
                twoPointers = twoPointersValueTextView.text.toString().toInt(),
                threePointers = threePointersValueTextView.text.toString().toInt(),
                freeThrows = freeThrowsValueTextView.text.toString().toInt(),
                playerId = player.playerId
            )
            updateTeamStatsInDatabase(oldPlayer, newPlayer) // Update team stats based on player updates
            finish() // Finish the current activity to return to the previous one
        }

    }

    private fun initializeViews() {
        // Initialize all your TextViews and Buttons here
        reboundsValueTextView = findViewById(R.id.reboundsValueTextView)
        assistsValueTextView = findViewById(R.id.assistsValueTextView)
        twoPointersValueTextView = findViewById(R.id.twoPointersValueTextView)
        threePointersValueTextView = findViewById(R.id.threePointersValueTextView)
        freeThrowsValueTextView = findViewById(R.id.freeThrowsValueTextView)

        reboundsIncrementButton = findViewById(R.id.reboundsIncrementButton)
        assistsIncrementButton = findViewById(R.id.assistsIncrementButton)
        twoPointersIncrementButton = findViewById(R.id.twoPointersIncrementButton)
        threePointersIncrementButton = findViewById(R.id.threePointersIncrementButton)
        freeThrowsIncrementButton = findViewById(R.id.freeThrowsIncrementButton)

        reboundsDecrementButton = findViewById(R.id.reboundsDecrementButton)
        assistsDecrementButton = findViewById(R.id.assistsDecrementButton)
        twoPointersDecrementButton = findViewById(R.id.twoPointersDecrementButton)
        threePointersDecrementButton = findViewById(R.id.threePointersDecrementButton)
        freeThrowsDecrementButton = findViewById(R.id.freeThrowsDecrementButton)

        submitButton = findViewById(R.id.submitButton)
    }

    private fun setupListeners() {
        setupIncrementDecrementListeners(reboundsIncrementButton, reboundsDecrementButton, reboundsValueTextView)
        setupIncrementDecrementListeners(assistsIncrementButton, assistsDecrementButton, assistsValueTextView)
        setupIncrementDecrementListeners(twoPointersIncrementButton, twoPointersDecrementButton, twoPointersValueTextView)
        setupIncrementDecrementListeners(threePointersIncrementButton, threePointersDecrementButton, threePointersValueTextView)
        setupIncrementDecrementListeners(freeThrowsIncrementButton, freeThrowsDecrementButton, freeThrowsValueTextView)

        submitButton.setOnClickListener {
            updateStatsInDatabase()
        }
    }

    private fun setupIncrementDecrementListeners(incrementButton: Button, decrementButton: Button, valueTextView: TextView) {
        incrementButton.setOnClickListener {
            val currentValue = valueTextView.text.toString().toIntOrNull() ?: 0
            valueTextView.text = (currentValue + 1).toString()
        }

        decrementButton.setOnClickListener {
            val currentValue = valueTextView.text.toString().toIntOrNull() ?: 0
            if (currentValue > 0) {
                valueTextView.text = (currentValue - 1).toString()
            }
        }
    }

    private fun fetchPlayerStats() {
        val playerRef = FirebaseDatabase.getInstance().getReference("players/$teamId/$playerId")
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val player = dataSnapshot.getValue(Player::class.java)
                if (player != null) {
                    // Update the TextViews with player stats
                    reboundsValueTextView.text = player.rebounds.toString()
                    assistsValueTextView.text = player.assists.toString()
                    twoPointersValueTextView.text = player.twoPointers.toString()
                    threePointersValueTextView.text = player.threePointers.toString()
                    freeThrowsValueTextView.text = player.freeThrows.toString()
                    Log.e("IncrementSTatsActivity", player.rebounds.toString())
                } else {
                    Log.e("IncrementStatsActivity", "Player data is null")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("IncrementStatsActivity", "Failed to fetch player stats: ${databaseError.toException()}")
            }
        })
    }

    private fun updateStatsInDatabase() {
        updateStatInDatabase("rebounds", reboundsValueTextView.text.toString().toInt())
        updateStatInDatabase("assists", assistsValueTextView.text.toString().toInt())
        updateStatInDatabase("twoPointers", twoPointersValueTextView.text.toString().toInt())
        updateStatInDatabase("threePointers", threePointersValueTextView.text.toString().toInt())
        updateStatInDatabase("freeThrows", freeThrowsValueTextView.text.toString().toInt())
    }

    private fun updateStatInDatabase(statName: String, value: Int) {
        val statRef = FirebaseDatabase.getInstance().getReference("players/$teamId/$playerId/$statName")
        statRef.setValue(value)
            .addOnSuccessListener {
                Log.d("IncrementStatsActivity", "$statName updated successfully in the database for player $playerId")
            }
            .addOnFailureListener { exception ->
                Log.e("IncrementStatsActivity", "Failed to update $statName in the database for player $playerId: $exception")
            }
    }
    private fun updateTeamStatsInDatabase(oldPlayer: Player, newPlayer: Player) {
        val teamRef = FirebaseDatabase.getInstance().getReference("teams/$teamId")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val team = dataSnapshot.getValue(Team::class.java)
                if (team != null) {
                    val reboundsDiff = newPlayer.rebounds - oldPlayer.rebounds
                    val assistsDiff = newPlayer.assists - oldPlayer.assists
                    val twoPointersDiff = newPlayer.twoPointers - oldPlayer.twoPointers
                    val threePointersDiff = newPlayer.threePointers - oldPlayer.threePointers
                    val freeThrowsDiff = newPlayer.freeThrows - oldPlayer.freeThrows

                    val updatedTeam = team.copy(
                        rebounds = team.rebounds + reboundsDiff,
                        assists = team.assists + assistsDiff,
                        twoPointers = team.twoPointers + twoPointersDiff,
                        threePointers = team.threePointers + threePointersDiff,
                        freeThrows = team.freeThrows + freeThrowsDiff
                    )

                    teamRef.setValue(updatedTeam)
                        .addOnSuccessListener {
                            Log.d("IncrementStatsActivity", "Team stats updated successfully in the database")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("IncrementStatsActivity", "Failed to update team stats in the database: $exception")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("IncrementStatsActivity", "Failed to fetch team stats: ${databaseError.toException()}")
            }
        })
    }

}