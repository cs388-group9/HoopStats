package com.example.hoopstats

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.google.firebase.database.FirebaseDatabase

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
            gameIdTextView.text = "Game ID: ${game.gameId}"

            // Fetch and display team names
            fetchTeamNames(game.teamIds) { teamNames ->
                teamNamesTextView.text = teamNames.joinToString(" vs ")
            }

            gameButton.setOnClickListener {
                // Open TrackStats activity for the selected game
                val context = itemView.context
                val intent = Intent(context, TrackStatsActivity::class.java)
                intent.putExtra("gameId", game.gameId)
                context.startActivity(intent)
            }
        }

        private fun fetchTeamNames(teamIds: List<String>, callback: (List<String>) -> Unit) {
            val names = mutableListOf<String>()
            val dbRef = FirebaseDatabase.getInstance().getReference("teams")

            teamIds.forEach { teamId ->
                dbRef.child(teamId).get().addOnSuccessListener { dataSnapshot ->
                    val teamName = dataSnapshot.child("teamName").value.toString()
                    names.add(teamName)
                    if (names.size == teamIds.size) {
                        callback(names)
                    }
                }.addOnFailureListener {
                    names.add("Unknown Team")
                    if (names.size == teamIds.size) {
                        callback(names)
                    }
                }
            }
        }
    }
}
