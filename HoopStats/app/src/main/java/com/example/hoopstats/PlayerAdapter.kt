package com.example.hoopstats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Player

class PlayerAdapter(private val players: List<Player>) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item_layout, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int = players.size

    // ViewHolder class
    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerNameTextView: TextView = itemView.findViewById(R.id.playerNameTextView)
        private val pointsTextView: TextView = itemView.findViewById(R.id.pointsTextView)
        private val reboundsTextView: TextView = itemView.findViewById(R.id.reboundsTextView)
        private val assistsTextView: TextView = itemView.findViewById(R.id.assistsTextView)

        fun bind(player: Player) {
            playerNameTextView.text = player.playerName
            pointsTextView.text = player.totalPoints.toString()
            reboundsTextView.text = player.rebounds.toString()
            assistsTextView.text = player.assists.toString()
        }
    }
}
