package se.yourcompany.miljonaren.tv.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.yourcompany.miljonaren.data.repository.GameHistoryRepositoryFactory
import se.yourcompany.miljonaren.data.repository.LocalQuestionRepository
import se.yourcompany.miljonaren.domain.usecase.ApplyFiftyFiftyUseCase
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.SaveCompletedGameUseCase
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase
import se.yourcompany.miljonaren.domain.usecase.SubmitAnswerUseCase

class GameViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val questionRepository = LocalQuestionRepository()
            val gameHistoryRepository = GameHistoryRepositoryFactory.create(context)
            return GameViewModel(
                startGameUseCase = StartGameUseCase(),
                getNextQuestionUseCase = GetNextQuestionUseCase(questionRepository),
                applyFiftyFiftyUseCase = ApplyFiftyFiftyUseCase(),
                submitAnswerUseCase = SubmitAnswerUseCase(),
                advanceTurnUseCase = AdvanceTurnUseCase(),
                finishGameUseCase = FinishGameUseCase(),
                saveCompletedGameUseCase = SaveCompletedGameUseCase(gameHistoryRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
