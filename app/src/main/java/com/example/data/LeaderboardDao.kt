package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard WHERE difficulty = :difficulty ORDER BY timeInSeconds ASC, attempts ASC LIMIT 10")
    fun getTopScores(difficulty: String): Flow<List<LeaderboardEntry>>

    @Query("SELECT * FROM leaderboard ORDER BY timeInSeconds ASC, attempts ASC LIMIT 50")
    fun getAllScores(): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(entry: LeaderboardEntry)

    @Query("DELETE FROM leaderboard WHERE id = :id")
    suspend fun deleteScore(id: Int)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()
}
