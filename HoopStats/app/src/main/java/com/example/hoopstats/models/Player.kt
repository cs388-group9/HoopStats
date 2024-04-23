package com.example.hoopstats.models

import java.io.Serializable

data class Player(
    val playerName: String = "",
    val teamId: String = "",
    var rebounds: Int = 0,
    var assists: Int = 0,
    var twoPointers: Int = 0,
    var threePointers: Int = 0,
    var freeThrows: Int = 0,
    var playerId: String = "" // Added playerId field
) : Serializable {
    val totalPoints: Int
        get() = twoPointers * 2 + threePointers * 3 + freeThrows

    // Default constructor
    constructor() : this("", "", 0, 0, 0, 0, 0, "")
}
