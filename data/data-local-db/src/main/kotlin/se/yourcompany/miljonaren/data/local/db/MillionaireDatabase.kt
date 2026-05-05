package se.yourcompany.miljonaren.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import se.yourcompany.miljonaren.data.local.db.dao.GameHistoryDao
import se.yourcompany.miljonaren.data.local.db.entity.GameSessionEntity
import se.yourcompany.miljonaren.data.local.db.entity.PlayerResultEntity

@Database(
    entities = [
        GameSessionEntity::class,
        PlayerResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MillionaireDatabase : RoomDatabase() {

    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MillionaireDatabase? = null

        fun getInstance(context: Context): MillionaireDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MillionaireDatabase::class.java,
                    "millionaire.db"
                ).build().also { database ->
                    INSTANCE = database
                }
            }
        }
    }
}
