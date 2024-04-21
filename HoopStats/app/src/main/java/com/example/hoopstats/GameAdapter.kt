package com.example.hoopstats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item_layout, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount() = games.size

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameNameTextView: TextView = itemView.findViewById(R.id.gameNameTextView)
        private val gameIdTextView: TextView = itemView.findViewById(R.id.gameIdTextView)

        fun bind(game: Game) {
            gameNameTextView.text = game.gameName
            gameIdTextView.text = "Game ID: ${game.gameId}"
            // Optionally, handle the creatorUserId if needed in your UI
        }
    }
}
