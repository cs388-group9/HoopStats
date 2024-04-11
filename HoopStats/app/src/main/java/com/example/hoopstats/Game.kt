package com.example.hoopstats

class Game(
    var gameName: String,
    var numberOfPlayers: Int,
    var team1Name: String,
    var team2Name: String,
    var team1Players: ArrayList<String> = ArrayList(),
    var team2Players: ArrayList<String> = ArrayList()
)
