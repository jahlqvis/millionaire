package se.yourcompany.miljonaren.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey val sessionId: String,
    val completedAtEpochMs: Long,
    val totalPlayers: Int,
    val totalRounds: Int,
    val isTie: Boolean,
    val winnerName: String?
)
