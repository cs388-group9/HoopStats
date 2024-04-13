package com.example.hoopstats

class Game(
    var gameId: Int,
    var gameName: String,
    var numberOfPlayers: Int,
    var team1Name: String,
    var team2Name: String,
    var team1Players: ArrayList<String> = ArrayList(),
    var team2Players: ArrayList<String> = ArrayList()
) {
    companion object {
        private var nextGameId: Int = 10000

        // Function to get the next available game ID
        fun getNextGameId(): Int {
            return nextGameId++
        }
    }
}
