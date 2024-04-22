package com.example.hoopstats

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_button, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int = games.size

    // ViewHolder class
    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameButton: Button = itemView.findViewById(R.id.gameButton)
        private val teamNamesTextView: TextView = itemView.findViewById(R.id.teamNamesTextView)
        private val gameIdTextView: TextView = itemView.findViewById(R.id.gameIdTextView)

        fun bind(game: Game) {
            gameButton.text = game.gameName
            // Set team names
            teamNamesTextView.text = "${game.teamNames[0]} vs ${game.teamNames[1]}"
            // Set game ID
            gameIdTextView.text = "Game ID: ${game.gameId}"

            gameButton.setOnClickListener {
                // Open TrackStats activity for the selected game
                val context = itemView.context
                val intent = Intent(context, TrackStats::class.java)
                // Pass game data to TrackStats activity if needed
                intent.putExtra("gameId", game.gameId)
                intent.putExtra("gameName", game.gameName)
                intent.putExtra("team1Name", game.teamNames[0])
                intent.putExtra("team2Name", game.teamNames[1])
                context.startActivity(intent)
            }
        }

    }
}
