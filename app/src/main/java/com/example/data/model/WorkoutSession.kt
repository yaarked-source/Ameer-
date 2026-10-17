package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val discipline: String, // Boxing, Muay Thai, Kickboxing, MMA
    val totalRounds: Int,
    val completedRounds: Int,
    val durationSeconds: Int,
    val punchesThrown: Int,
    val caloriesBurned: Int,
    val dateTimestamp: Long = System.currentTimeMillis()
)
