package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_songs")
data class TrainingSong(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val videoId: String,
    val category: String, // "Hype Rap", "Muay Thai Beats", "Heavy Bag BPM", "Metal / Rock", "Warmup"
    val bpm: Int = 140,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val addedTimestamp: Long = System.currentTimeMillis()
)
