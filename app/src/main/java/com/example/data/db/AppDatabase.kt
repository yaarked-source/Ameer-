package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.TrainingSong
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TrainingSong::class, WorkoutSession::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shadowstrike_combat.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default combat audio & YouTube training tracks
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).songDao().insertSongs(initialSeedSongs)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val initialSeedSongs = listOf(
            TrainingSong(
                title = "Heavy Bag Phonk & Combat Drift",
                url = "https://www.youtube.com/watch?v=2WPCLda_erI",
                videoId = "2WPCLda_erI",
                category = "Hype Rap",
                bpm = 150,
                isFavorite = true,
                notes = "High-octane aggression beat for power combination rounds."
            ),
            TrainingSong(
                title = "Traditional Muay Thai Sarama Rhythm",
                url = "https://www.youtube.com/watch?v=kffacxfA7G4",
                videoId = "kffacxfA7G4",
                category = "Muay Thai Beats",
                bpm = 135,
                isFavorite = true,
                notes = "Authentic Pi Java tempo to pace roundhouse kicks & knees."
            ),
            TrainingSong(
                title = "140 BPM Boxer Speed Punch Metronome",
                url = "https://www.youtube.com/watch?v=7e8x0u0J4bE",
                videoId = "7e8x0u0J4bE",
                category = "Heavy Bag BPM",
                bpm = 140,
                isFavorite = false,
                notes = "Strict timing beat for crisp jab-cross rhythm drills."
            ),
            TrainingSong(
                title = "Champion Arena Hype Trap",
                url = "https://www.youtube.com/watch?v=kJQP7kiw5Fk",
                videoId = "kJQP7kiw5Fk",
                category = "Hype Rap",
                bpm = 148,
                isFavorite = true,
                notes = "Walkout style horns and heavy bass drops."
            ),
            TrainingSong(
                title = "Iron Conditioning Warmup Beats",
                url = "https://www.youtube.com/watch?v=jfKfPfyJRdk",
                videoId = "jfKfPfyJRdk",
                category = "Warmup",
                bpm = 120,
                isFavorite = false,
                notes = "Smooth steady pace for jump rope & dynamic stretching."
            ),
            TrainingSong(
                title = "Adrenaline Hardstyle Combat Mix",
                url = "https://www.youtube.com/watch?v=VB_3l90rV_A",
                videoId = "VB_3l90rV_A",
                category = "Metal / Rock",
                bpm = 160,
                isFavorite = false,
                notes = "Maximum heart-rate burn for the final championship round."
            )
        )
    }
}
