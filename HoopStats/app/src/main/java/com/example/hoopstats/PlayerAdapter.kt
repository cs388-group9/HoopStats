package com.example.hoopstats

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Player

class PlayerAdapter(
    private var players: MutableList<Player> = mutableListOf(),
    private val userID: String?,
    private val submittedByUserIds: Array<String> // New parameter to accept user IDs array
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    fun updatePlayers(players: List<Player>) {
        this.players.clear()
        this.players.addAll(players)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item_layout, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int = players.size

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

            val isCurrentUserInArray = userID != null && submittedByUserIds.contains(userID)
            Log.d("UserIDLog", "Array size: ${submittedByUserIds.size}")
            submittedByUserIds.forEach { userId ->
                Log.e("#############", "User ID: $userId")
            }

            if (isCurrentUserInArray) {
                playerNameButton.isEnabled = false
            } else {
                playerNameButton.setOnClickListener {
                    val intent = Intent(itemView.context, IncrementStatsActivity::class.java)
                    intent.putExtra("player", player)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }
}
