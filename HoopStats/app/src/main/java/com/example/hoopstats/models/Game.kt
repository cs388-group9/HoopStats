package com.example.hoopstats.models

data class Game(
    val gameId: String = "",
    val gameName: String = "",
    val teamIds: List<String> = listOf(),
    val creatorUserId: String = "",
    val submittedByUserIds: List<String> = listOf()
)
