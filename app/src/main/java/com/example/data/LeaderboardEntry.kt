package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val difficulty: String,
    val timeInSeconds: Int,
    val attempts: Int,
    val timestamp: Long = System.currentTimeMillis()
)
