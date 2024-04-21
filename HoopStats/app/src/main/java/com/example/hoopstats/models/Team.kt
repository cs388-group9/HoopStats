// File: Team.kt
package com.example.hoopstats.models

data class Team(
    val teamId: String = "",   // Initialize with default values to satisfy Firebase requirements
    val gameId: String = "",
    val teamName: String = ""
)
