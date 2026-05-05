package se.yourcompany.miljonaren.tv.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.yourcompany.miljonaren.data.repository.GameHistoryRepositoryFactory
import se.yourcompany.miljonaren.domain.usecase.GetGameResultsUseCase
import se.yourcompany.miljonaren.domain.usecase.GetRecentGamesUseCase

class HistoryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            val repository = GameHistoryRepositoryFactory.create(context)
            return HistoryViewModel(
                getRecentGamesUseCase = GetRecentGamesUseCase(repository),
                getGameResultsUseCase = GetGameResultsUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
