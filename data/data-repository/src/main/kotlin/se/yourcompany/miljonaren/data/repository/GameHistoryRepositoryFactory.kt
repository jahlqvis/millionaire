package se.yourcompany.miljonaren.data.repository

import android.content.Context
import se.yourcompany.miljonaren.data.local.db.MillionaireDatabase
import se.yourcompany.miljonaren.domain.usecase.GameHistoryRepository

object GameHistoryRepositoryFactory {
    fun create(context: Context): GameHistoryRepository {
        val database = MillionaireDatabase.getInstance(context)
        return RoomGameHistoryRepository(database.gameHistoryDao())
    }
}
