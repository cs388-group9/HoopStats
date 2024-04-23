package com.example.hoopstats.models

data class Team(
    val teamId: String = "",
    val teamName: String = "",
    val playerIds: List<String> = listOf(),
    val gameId: String = "",
    var rebounds: Int = 0,
    var assists: Int = 0,
    var twoPointers: Int = 0,
    var threePointers: Int = 0,
    var freeThrows: Int = 0,
    var totalPoints: Int = 0

)
