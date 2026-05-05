package se.yourcompany.miljonaren.domain.model

data class GameHistoryEntry(
    val sessionId: String,
    val completedAtEpochMs: Long,
    val totalPlayers: Int,
    val totalRounds: Int,
    val isTie: Boolean,
    val winnerName: String?
)

data class PlayerHistoryResult(
    val sessionId: String,
    val playerId: String,
    val playerName: String,
    val placement: Int,
    val score: Int
)
