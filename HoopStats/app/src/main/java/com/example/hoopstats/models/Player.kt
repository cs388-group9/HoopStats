package com.example.hoopstats.models

data class Player(
    val playerName: String,
    val rebounds: Int = 0,
    val assists: Int = 0,
    val twoPointers: Int = 0,
    val threePointers: Int = 0,
    val freeThrows: Int = 0
) {
    val totalPoints: Int
        get() = twoPointers * 2 + threePointers * 3 + freeThrows  // Assuming 2 points for twoPointers, 3 for threePointers
}
