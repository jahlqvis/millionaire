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
import androidx.compose.ui.res.stringResource
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
import se.yourcompany.miljonaren.tv.game.AnswerFeedback
import se.yourcompany.miljonaren.tv.game.GameViewModel

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
    val defaultPlayerNames = listOf(
        stringResource(id = R.string.default_player_name, 1),
        stringResource(id = R.string.default_player_name, 2),
        stringResource(id = R.string.default_player_name, 3),
        stringResource(id = R.string.default_player_name, 4)
    )
    val minPlayersValidationError = stringResource(id = R.string.player_setup_validation_min_one)
    var playerCount by remember { mutableIntStateOf(2) }
    var playerNames by remember { mutableStateOf(defaultPlayerNames) }
    var playerSetupError by remember { mutableStateOf<String?>(null) }

    fun resetPlayerSetupForm() {
        playerCount = 2
        playerNames = defaultPlayerNames
        playerSetupError = null
    }

    val playerSetupState = PlayerSetupUiState(
        title = stringResource(id = R.string.player_setup_title),
        playerNameLabels = List(playerCount) { index ->
            stringResource(id = R.string.player_setup_player_label, index + 1)
        },
        backButtonLabel = stringResource(id = R.string.player_setup_back),
        startButtonLabel = stringResource(id = R.string.player_setup_start),
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
                    title = stringResource(id = R.string.app_name),
                    subtitle = stringResource(id = R.string.home_subtitle),
                    primaryActionLabel = stringResource(id = R.string.home_start_game)
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
                        name.trim().ifBlank { defaultPlayerNames[index] }
                    }

                    if (selectedNames.isEmpty()) {
                        playerSetupError = minPlayersValidationError
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
                Text(text = stringResource(id = R.string.gameplay_empty_state))
            } else {
                val answerFeedback = when (uiState.answerFeedback) {
                    AnswerFeedback.CORRECT -> stringResource(id = R.string.gameplay_feedback_correct)
                    AnswerFeedback.WRONG -> stringResource(id = R.string.gameplay_feedback_wrong)
                    null -> null
                }
                val isAnswerCorrect = when (uiState.answerFeedback) {
                    AnswerFeedback.CORRECT -> true
                    AnswerFeedback.WRONG -> false
                    null -> null
                }

                val gameplayState = GameplayUiState(
                    roundLabel = stringResource(
                        id = R.string.gameplay_round,
                        activeSession.currentRound,
                        activeSession.maxRounds
                    ),
                    turnLabel = stringResource(
                        id = R.string.gameplay_turn,
                        activeSession.activePlayer.name
                    ),
                    questionLabel = stringResource(
                        id = R.string.gameplay_question,
                        activeQuestion.textSv
                    ),
                    options = activeQuestion.options.map { option ->
                        GameplayOptionUiState(
                            id = option.id,
                            displayText = stringResource(
                                id = R.string.gameplay_option,
                                option.id,
                                option.textSv
                            )
                        )
                    },
                    answerFeedback = answerFeedback,
                    isAnswerCorrect = isAnswerCorrect,
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
                Text(text = stringResource(id = R.string.results_empty_state))
            } else {
                val resultsState = ResultsUiState(
                    title = stringResource(id = R.string.results_title),
                    isTie = gameResult.isTie,
                    tieLabel = stringResource(id = R.string.results_tie),
                    winnerLabel = stringResource(
                        id = R.string.results_winner,
                        gameResult.winner?.name ?: "-"
                    ),
                    restartLabel = stringResource(id = R.string.results_restart),
                    players = gameResult.rankedPlayers.mapIndexed { index, player ->
                        ResultsPlayerUiState(
                            placement = index + 1,
                            name = player.name,
                            score = player.score,
                            rowLabel = stringResource(
                                id = R.string.results_score_row,
                                index + 1,
                                player.name,
                                player.score
                            )
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
