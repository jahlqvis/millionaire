package se.yourcompany.miljonaren.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "player_results",
    primaryKeys = ["sessionId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = GameSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class PlayerResultEntity(
    val sessionId: String,
    val playerId: String,
    val playerName: String,
    val placement: Int,
    val score: Int
)
