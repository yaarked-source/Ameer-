package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_sessions ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions")
    fun getTotalCalories(): Flow<Int?>

    @Query("SELECT SUM(punchesThrown) FROM workout_sessions")
    fun getTotalPunches(): Flow<Int?>

    @Query("SELECT SUM(durationSeconds) FROM workout_sessions")
    fun getTotalSeconds(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM workout_sessions")
    fun getTotalWorkoutsCount(): Flow<Int>
}
