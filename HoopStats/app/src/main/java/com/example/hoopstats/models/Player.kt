package com.example.hoopstats.models

import java.io.Serializable

data class Player(
    val playerName: String,
    val team: Int,
    var rebounds: Int,
    var assists: Int,
    var twoPointers: Int,
    var threePointers: Int,
    var freeThrows: Int
) : Serializable {
    val totalPoints: Int
        get() = twoPointers + threePointers + freeThrows
}
