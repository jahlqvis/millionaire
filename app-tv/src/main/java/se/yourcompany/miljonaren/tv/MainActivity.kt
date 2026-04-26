package se.yourcompany.miljonaren.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.yourcompany.miljonaren.core.navigation.AppRoutes
import se.yourcompany.miljonaren.feature.gameplay.GameplayOptionUiState
import se.yourcompany.miljonaren.feature.gameplay.GameplayScreen
import se.yourcompany.miljonaren.feature.gameplay.GameplayUiState
import se.yourcompany.miljonaren.feature.home.HomeUiState
import se.yourcompany.miljonaren.feature.home.HomeScreen
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupUiState
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupScreen
import se.yourcompany.miljonaren.feature.results.ResultsPlayerUiState
import se.yourcompany.miljonaren.feature.results.ResultsScreen
import se.yourcompany.miljonaren.feature.results.ResultsUiState
import se.yourcompany.miljonaren.tv.game.GameViewModel

private val defaultPlayerNames = listOf("Spelare 1", "Spelare 2", "Spelare 3", "Spelare 4")

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
    val gameViewModel: GameViewModel = viewModel()
    val uiState by gameViewModel.uiState.collectAsState()
    var playerCount by remember { mutableIntStateOf(2) }
    var playerNames by remember { mutableStateOf(defaultPlayerNames) }
    var playerSetupError by remember { mutableStateOf<String?>(null) }

    fun resetPlayerSetupForm() {
        playerCount = 2
        playerNames = defaultPlayerNames
        playerSetupError = null
    }

    val playerSetupState = PlayerSetupUiState(
        playerCount = playerCount,
        names = playerNames,
        validationError = playerSetupError
    )

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            navController.navigate(AppRoutes.RESULTS) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(AppRoutes.HOME) {
            HomeScreen(
                state = HomeUiState(
                    title = "Miljonaren TV",
                    primaryActionLabel = "Starta spel"
                ),
                onStartGame = {
                    resetPlayerSetupForm()
                    navController.navigate(AppRoutes.PLAYER_SETUP) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.PLAYER_SETUP) {
            PlayerSetupScreen(
                state = playerSetupState,
                onPlayerCountChanged = { updatedCount ->
                    playerCount = updatedCount
                },
                onPlayerNameChanged = { index, newName ->
                    playerNames = playerNames.toMutableList().apply {
                        this[index] = newName
                    }
                },
                onStartGame = {
                    val selectedNames = playerNames.take(playerCount).mapIndexed { index, name ->
                        name.trim().ifBlank { "Spelare ${index + 1}" }
                    }

                    if (selectedNames.isEmpty()) {
                        playerSetupError = "Minst en spelare kravs"
                        return@PlayerSetupScreen
                    }

                    playerSetupError = null
                    gameViewModel.startGame(selectedNames)
                    if (gameViewModel.uiState.value.result != null) {
                        navController.navigate(AppRoutes.RESULTS) {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(AppRoutes.GAMEPLAY) {
                            launchSingleTop = true
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.GAMEPLAY) {
            val activeSession = uiState.session
            val activeQuestion = uiState.currentQuestion
            if (activeSession == null || activeQuestion == null) {
                Text(text = "Ingen aktiv omgang")
            } else {
                val gameplayState = GameplayUiState(
                    currentRound = activeSession.currentRound,
                    maxRounds = activeSession.maxRounds,
                    activePlayerName = activeSession.activePlayer.name,
                    questionText = activeQuestion.textSv,
                    options = activeQuestion.options.map { option ->
                        GameplayOptionUiState(
                            id = option.id,
                            text = option.textSv
                        )
                    },
                    answerFeedback = uiState.answerFeedback,
                    answerLocked = uiState.answerLocked
                )

                GameplayScreen(
                    state = gameplayState,
                    onAnswerSelected = { selectedOptionId ->
                        gameViewModel.submitAnswer(selectedOptionId)
                    }
                )
            }
        }

        composable(AppRoutes.RESULTS) {
            val gameResult = uiState.result
            if (gameResult == null) {
                Text(text = "Inget resultat")
            } else {
                val resultsState = ResultsUiState(
                    isTie = gameResult.isTie,
                    winnerName = gameResult.winner?.name,
                    players = gameResult.rankedPlayers.mapIndexed { index, player ->
                        ResultsPlayerUiState(
                            placement = index + 1,
                            name = player.name,
                            score = player.score
                        )
                    }
                )

                ResultsScreen(
                    state = resultsState,
                    onRestart = {
                        gameViewModel.restartGame()
                        resetPlayerSetupForm()
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
