package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hoopstats.R
import com.example.hoopstats.models.Player

class IncrementStatsActivity : AppCompatActivity() {

    private lateinit var playerNameTextView: TextView
    private lateinit var reboundsValueTextView: TextView
    private lateinit var assistsValueTextView: TextView
    private lateinit var twoPointersValueTextView: TextView
    private lateinit var threePointersValueTextView: TextView
    private lateinit var freeThrowsValueTextView: TextView
    private lateinit var reboundsDecrementButton: Button
    private lateinit var reboundsIncrementButton: Button
    private lateinit var assistsDecrementButton: Button
    private lateinit var assistsIncrementButton: Button
    private lateinit var twoPointersDecrementButton: Button
    private lateinit var twoPointersIncrementButton: Button
    private lateinit var threePointersDecrementButton: Button
    private lateinit var threePointersIncrementButton: Button
    private lateinit var freeThrowsDecrementButton: Button
    private lateinit var freeThrowsIncrementButton: Button
    private lateinit var submitButton: Button

    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_increment_stats)

        playerNameTextView = findViewById(R.id.titleTextView)
        reboundsValueTextView = findViewById(R.id.reboundsValueTextView)
        assistsValueTextView = findViewById(R.id.assistsValueTextView)
        twoPointersValueTextView = findViewById(R.id.twoPointersValueTextView)
        threePointersValueTextView = findViewById(R.id.threePointersValueTextView)
        freeThrowsValueTextView = findViewById(R.id.freeThrowsValueTextView)
        reboundsDecrementButton = findViewById(R.id.reboundsDecrementButton)
        reboundsIncrementButton = findViewById(R.id.reboundsIncrementButton)
        assistsDecrementButton = findViewById(R.id.assistsDecrementButton)
        assistsIncrementButton = findViewById(R.id.assistsIncrementButton)
        twoPointersDecrementButton = findViewById(R.id.twoPointersDecrementButton)
        twoPointersIncrementButton = findViewById(R.id.twoPointersIncrementButton)
        threePointersDecrementButton = findViewById(R.id.threePointersDecrementButton)
        threePointersIncrementButton = findViewById(R.id.threePointersIncrementButton)
        freeThrowsDecrementButton = findViewById(R.id.freeThrowsDecrementButton)
        freeThrowsIncrementButton = findViewById(R.id.freeThrowsIncrementButton)
        submitButton = findViewById(R.id.submitButton)

        player = intent.getSerializableExtra("player") as? Player ?: Player("", 0, 0, 0, 0, 0, 0)

        playerNameTextView.text = player.playerName
        reboundsValueTextView.text = player.rebounds.toString()
        assistsValueTextView.text = player.assists.toString()
        twoPointersValueTextView.text = player.twoPointers.toString()
        threePointersValueTextView.text = player.threePointers.toString()
        freeThrowsValueTextView.text = player.freeThrows.toString()

        reboundsDecrementButton.setOnClickListener { decrementStat(reboundsValueTextView) }
        reboundsIncrementButton.setOnClickListener { incrementStat(reboundsValueTextView) }
        assistsDecrementButton.setOnClickListener { decrementStat(assistsValueTextView) }
        assistsIncrementButton.setOnClickListener { incrementStat(assistsValueTextView) }
        twoPointersDecrementButton.setOnClickListener { decrementStat(twoPointersValueTextView) }
        twoPointersIncrementButton.setOnClickListener { incrementStat(twoPointersValueTextView) }
        threePointersDecrementButton.setOnClickListener { decrementStat(threePointersValueTextView) }
        threePointersIncrementButton.setOnClickListener { incrementStat(threePointersValueTextView) }
        freeThrowsDecrementButton.setOnClickListener { decrementStat(freeThrowsValueTextView) }
        freeThrowsIncrementButton.setOnClickListener { incrementStat(freeThrowsValueTextView) }

        submitButton.setOnClickListener {
            // Update player stats with the values from TextViews
            player.rebounds = reboundsValueTextView.text.toString().toInt()
            player.assists = assistsValueTextView.text.toString().toInt()
            player.twoPointers = twoPointersValueTextView.text.toString().toInt()
            player.threePointers = threePointersValueTextView.text.toString().toInt()
            player.freeThrows = freeThrowsValueTextView.text.toString().toInt()

            Log.d("IncrementStatsActivity", "Updated Player Stats: $player")

            // Return the updated player object to the calling activity
            val intent = Intent()
            intent.putExtra("updatedPlayer", player)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun incrementStat(textView: TextView) {
        var value = textView.text.toString().toInt()
        value++
        textView.text = value.toString()
    }

    private fun decrementStat(textView: TextView) {
        var value = textView.text.toString().toInt()
        if (value > 0) {
            value--
            textView.text = value.toString()
        }
    }
}

