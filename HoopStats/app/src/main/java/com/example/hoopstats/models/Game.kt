// File: Game.kt
package com.example.hoopstats.models

data class Game(
    val gameId: String = "",   // Initialize with default values to satisfy Firebase requirements
    val gameName: String = "",
    val teamNames: List<String> = emptyList()
)
