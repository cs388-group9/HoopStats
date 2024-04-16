// File: Game.kt
package com.example.hoopstats.models

data class Game(
    val gameId: String = "",
    val gameName: String = "",
    val teamNames: List<String> = emptyList(),
    val creatorUserId: String = ""
)
