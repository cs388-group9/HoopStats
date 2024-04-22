package com.example.hoopstats.models

data class Player(
    val playerName: String,
    val team: Int,
    val rebounds: Int,
    val assists: Int,
    val twoPointers: Int,
    val threePointers: Int,
    val freeThrows: Int,
) {
    val totalPoints: Int
        get() = twoPointers + threePointers + freeThrows
}
