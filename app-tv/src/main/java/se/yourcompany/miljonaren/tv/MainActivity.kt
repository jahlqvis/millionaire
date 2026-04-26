package se.yourcompany.miljonaren.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.yourcompany.miljonaren.core.navigation.AppRoutes
import se.yourcompany.miljonaren.data.repository.LocalQuestionRepository
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase
import se.yourcompany.miljonaren.domain.usecase.SubmitAnswerUseCase
import se.yourcompany.miljonaren.feature.gameplay.GameplayScreen
import se.yourcompany.miljonaren.feature.home.HomeScreen
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupScreen
import se.yourcompany.miljonaren.feature.results.ResultsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MillionaireTvRoot()
        }
    }
}

@Composable
private fun MillionaireTvRoot() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val questionRepository = remember { LocalQuestionRepository() }
    val startGameUseCase = remember { StartGameUseCase() }
    val getNextQuestionUseCase = remember { GetNextQuestionUseCase(questionRepository) }
    val submitAnswerUseCase = remember { SubmitAnswerUseCase() }
    val advanceTurnUseCase = remember { AdvanceTurnUseCase() }
    val finishGameUseCase = remember { FinishGameUseCase() }

    var session by remember { mutableStateOf<GameSession?>(null) }
    var currentQuestion by remember { mutableStateOf<Question?>(null) }
    var answerFeedback by remember { mutableStateOf<String?>(null) }
    var answerLocked by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<GameResult?>(null) }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(AppRoutes.HOME) {
            HomeScreen(
                onStartGame = {
                    navController.navigate(AppRoutes.PLAYER_SETUP)
                }
            )
        }

        composable(AppRoutes.PLAYER_SETUP) {
            PlayerSetupScreen(
                onStartGame = { playerNames ->
                    val startedSession = startGameUseCase(playerNames)
                    val next = getNextQuestionUseCase(startedSession)

                    session = next.updatedSession
                    currentQuestion = next.question
                    answerFeedback = null
                    answerLocked = false

                    if (next.question == null) {
                        result = finishGameUseCase(next.updatedSession)
                        navController.navigate(AppRoutes.RESULTS)
                    } else {
                        result = null
                        navController.navigate(AppRoutes.GAMEPLAY)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.GAMEPLAY) {
            val activeSession = session
            val activeQuestion = currentQuestion
            if (activeSession == null || activeQuestion == null) {
                Text(text = "Ingen aktiv omgang")
            } else {
                GameplayScreen(
                    activePlayer = activeSession.activePlayer,
                    currentRound = activeSession.currentRound,
                    maxRounds = activeSession.maxRounds,
                    question = activeQuestion,
                    answerFeedback = answerFeedback,
                    answerLocked = answerLocked,
                    onAnswerSelected = { selectedOptionId ->
                        if (answerLocked) {
                            return@GameplayScreen
                        }

                        val evaluated = submitAnswerUseCase(
                            session = activeSession,
                            question = activeQuestion,
                            selectedOptionId = selectedOptionId
                        )
                        session = evaluated.updatedSession
                        answerFeedback = if (evaluated.isCorrect) "Ratt svar!" else "Fel svar"
                        answerLocked = true

                        scope.launch {
                            delay(1200)
                            val progressed = advanceTurnUseCase(evaluated.updatedSession)

                            if (progressed.status == GameStatus.FINISHED) {
                                session = progressed
                                result = finishGameUseCase(progressed)
                                answerLocked = false
                                answerFeedback = null
                                navController.navigate(AppRoutes.RESULTS)
                                return@launch
                            }

                            val next = getNextQuestionUseCase(progressed)
                            session = next.updatedSession
                            currentQuestion = next.question
                            answerLocked = false
                            answerFeedback = null

                            if (next.question == null || next.updatedSession.status == GameStatus.FINISHED) {
                                result = finishGameUseCase(next.updatedSession)
                                navController.navigate(AppRoutes.RESULTS)
                            }
                        }
                    }
                )
            }
        }

        composable(AppRoutes.RESULTS) {
            val gameResult = result
            if (gameResult == null) {
                Text(text = "Inget resultat")
            } else {
                ResultsScreen(
                    result = gameResult,
                    onRestart = {
                        session = null
                        currentQuestion = null
                        answerFeedback = null
                        answerLocked = false
                        result = null
                        navController.navigate(AppRoutes.HOME)
                    }
                )
            }
        }
    }
}
