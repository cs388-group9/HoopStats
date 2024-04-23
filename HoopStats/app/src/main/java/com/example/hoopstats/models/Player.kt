package com.example.hoopstats.models

import java.io.Serializable

data class Player(
    val playerName: String,
    var rebounds: Int = 0,
    var assists: Int = 0,
    var twoPointers: Int = 0,
    var threePointers: Int = 0,
    var freeThrows: Int = 0
) : Serializable {
    val totalPoints: Int
        get() = twoPointers * 2 + threePointers * 3 + freeThrows  // Assuming 2 points for twoPointers, 3 for threePointers
}
