package com.example.hoopstats.models

data class Team(
    val teamId: String = "",
    val teamName: String = "",
    val playerIds: List<String> = listOf(),
    val gameId: String = ""
)
