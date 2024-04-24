package com.example.hoopstats

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.google.firebase.auth.FirebaseAuth
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

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameButton: Button = itemView.findViewById(R.id.gameButton)
        private val teamNamesTextView: TextView = itemView.findViewById(R.id.teamNamesTextView)
        private val gameIdTextView: TextView = itemView.findViewById(R.id.gameIdTextView)
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        fun bind(game: Game) {
            gameButton.text = game.gameName
            gameIdTextView.text = "Game ID: ${game.gameId}"

            fetchTeamNames(game.teamIds) { teamNames ->
                teamNamesTextView.text = teamNames.joinToString(" vs ")
            }

            // Show the game if the current user's ID is either in the creatorUserId field or submittedByUserIds array
            if (game.creatorUserId == userId || game.submittedByUserIds.contains(userId)) {
                gameButton.visibility = View.VISIBLE
                gameIdTextView.visibility = View.VISIBLE
                teamNamesTextView.visibility = View.VISIBLE
            } else {
                gameButton.visibility = View.GONE
                gameIdTextView.visibility = View.GONE
                teamNamesTextView.visibility = View.GONE
            }

            gameButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, TrackStatsActivity::class.java)
                intent.putExtra("gameId", game.gameId)
                // Pass the array of user IDs as an extra
                intent.putExtra("submittedByUserIds", game.submittedByUserIds.toTypedArray())
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
