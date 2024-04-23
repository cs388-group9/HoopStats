package com.example.hoopstats

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Player

class PlayerAdapter(private val context: Context, private val players: MutableList<Player>) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    // Setters and getters for players list
    fun setPlayers(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)
        notifyDataSetChanged()
    }

    private var itemClickListener: ((Player) -> Unit)? = null

    fun setOnItemClickListener(listener: (Player) -> Unit) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item_layout, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player)
        Log.d("PlayerAdapter", "Player bound: ${player.playerName}")
    }

    override fun getItemCount(): Int = players.size

    // ViewHolder class
    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerNameButton: Button = itemView.findViewById(R.id.playerNameButton)
        private val pointsTextView: TextView = itemView.findViewById(R.id.pointsTextView)
        private val reboundsTextView: TextView = itemView.findViewById(R.id.reboundsTextView)
        private val assistsTextView: TextView = itemView.findViewById(R.id.assistsTextView)

        fun bind(player: Player) {
            playerNameButton.text = player.playerName
            pointsTextView.text = player.totalPoints.toString()
            reboundsTextView.text = player.rebounds.toString()
            assistsTextView.text = player.assists.toString()

            // Set click listener to the player name button
            playerNameButton.setOnClickListener {
                // Create an intent to start IncrementStatsActivity
                val intent = Intent(context, IncrementStatsActivity::class.java)
                // Pass player's data to IncrementStatsActivity
                intent.putExtra("player", player)
                // Start IncrementStatsActivity
                context.startActivity(intent)
            }
        }
    }
}
