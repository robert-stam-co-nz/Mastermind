package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val leaderboardDao: LeaderboardDao) {

    fun getTopScores(difficulty: String): Flow<List<LeaderboardEntry>> =
        leaderboardDao.getTopScores(difficulty)

    val allScores: Flow<List<LeaderboardEntry>> =
        leaderboardDao.getAllScores()

    suspend fun insertScore(entry: LeaderboardEntry) {
        leaderboardDao.insertScore(entry)
    }

    suspend fun deleteScore(id: Int) {
        leaderboardDao.deleteScore(id)
    }

    suspend fun clearLeaderboard() {
        leaderboardDao.clearLeaderboard()
    }
}
