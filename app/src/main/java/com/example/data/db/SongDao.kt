package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TrainingSong
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM training_songs ORDER BY id DESC")
    fun getAllSongs(): Flow<List<TrainingSong>>

    @Query("SELECT * FROM training_songs WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteSongs(): Flow<List<TrainingSong>>

    @Query("SELECT * FROM training_songs WHERE category = :cat ORDER BY id DESC")
    fun getSongsByCategory(cat: String): Flow<List<TrainingSong>>

    @Query("SELECT * FROM training_songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: Long): TrainingSong?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: TrainingSong): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<TrainingSong>)

    @Update
    suspend fun updateSong(song: TrainingSong)

    @Delete
    suspend fun deleteSong(song: TrainingSong)

    @Query("SELECT COUNT(*) FROM training_songs")
    suspend fun count(): Int
}
