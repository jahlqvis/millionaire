package se.yourcompany.miljonaren.tv

import android.os.Bundle
import java.text.DateFormat
import java.util.Date
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import se.yourcompany.miljonaren.core.audio.SoundPoolGameAudioPlayer
import se.yourcompany.miljonaren.domain.model.LifelineType
import se.yourcompany.miljonaren.core.navigation.AppRoutes
import se.yourcompany.miljonaren.feature.gameplay.GameplayOptionUiState
import se.yourcompany.miljonaren.feature.gameplay.AudienceVoteUiState
import se.yourcompany.miljonaren.feature.gameplay.LifelineAdviceUiState
import se.yourcompany.miljonaren.feature.gameplay.GameplayScreen
import se.yourcompany.miljonaren.feature.gameplay.GameplayUiState
import se.yourcompany.miljonaren.feature.history.HistoryGameItemUiState
import se.yourcompany.miljonaren.feature.history.HistoryPlayerItemUiState
import se.yourcompany.miljonaren.feature.history.HistoryScreen
import se.yourcompany.miljonaren.feature.history.HistoryUiState
import se.yourcompany.miljonaren.feature.home.HomeUiState
import se.yourcompany.miljonaren.feature.home.HomeScreen
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupUiState
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupScreen
import se.yourcompany.miljonaren.feature.results.ResultsPlayerUiState
import se.yourcompany.miljonaren.feature.results.ResultsScreen
import se.yourcompany.miljonaren.feature.results.ResultsUiState
import se.yourcompany.miljonaren.tv.game.AnswerFeedback
import se.yourcompany.miljonaren.tv.game.GameViewModel
import se.yourcompany.miljonaren.tv.game.GameViewModelFactory
import se.yourcompany.miljonaren.tv.history.HistoryViewModel
import se.yourcompany.miljonaren.tv.history.HistoryViewModelFactory

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
    val context = LocalContext.current
    val gameViewModelFactory = remember(context) {
        GameViewModelFactory(context.applicationContext)
    }
    val historyViewModelFactory = remember(context) {
        HistoryViewModelFactory(context.applicationContext)
    }
    val audioPlayer = remember(context) {
        SoundPoolGameAudioPlayer(context.applicationContext)
    }
    val gameViewModel: GameViewModel = viewModel(factory = gameViewModelFactory)
    val historyViewModel: HistoryViewModel = viewModel(factory = historyViewModelFactory)
    val uiState by gameViewModel.uiState.collectAsState()
    val historyState by historyViewModel.state.collectAsState()
    val defaultPlayerNames = listOf(
        stringResource(id = R.string.default_player_name, 1),
        stringResource(id = R.string.default_player_name, 2),
        stringResource(id = R.string.default_player_name, 3),
        stringResource(id = R.string.default_player_name, 4)
    )
    val minPlayersValidationError = stringResource(id = R.string.player_setup_validation_min_one)
    var playerCount by remember { mutableIntStateOf(2) }
    var roundCount by remember { mutableIntStateOf(5) }
    var timerEnabled by remember { mutableStateOf(false) }
    var showDiscardConfirmation by remember { mutableStateOf(false) }
    var playerNames by remember { mutableStateOf(defaultPlayerNames) }
    var playerSetupError by remember { mutableStateOf<String?>(null) }

    fun resetPlayerSetupForm() {
        playerCount = 2
        roundCount = 5
        timerEnabled = false
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
        roundsLabel = stringResource(id = R.string.player_setup_rounds),
        roundCount = roundCount,
        timerLabel = stringResource(id = R.string.player_setup_timer),
        timerOffLabel = stringResource(id = R.string.player_setup_timer_off),
        timerOnLabel = stringResource(id = R.string.player_setup_timer_on),
        timerEnabled = timerEnabled,
        playerCount = playerCount,
        names = playerNames,
        validationError = playerSetupError
    )

    DisposableEffect(audioPlayer) {
        onDispose {
            audioPlayer.release()
        }
    }

    LaunchedEffect(gameViewModel) {
        gameViewModel.audioEvents.collectLatest { soundEffect ->
            audioPlayer.play(soundEffect)
        }
    }

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            navController.navigate(AppRoutes.RESULTS) {
                launchSingleTop = true
            }
        }
    }

    fun openPlayerSetup() {
        resetPlayerSetupForm()
        navController.navigate(AppRoutes.PLAYER_SETUP) { launchSingleTop = true }
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text(stringResource(id = R.string.home_discard_title)) },
            text = { Text(stringResource(id = R.string.home_discard_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardConfirmation = false
                    gameViewModel.restartGame()
                    openPlayerSetup()
                }) { Text(stringResource(id = R.string.home_discard_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) {
                    Text(stringResource(id = R.string.home_discard_cancel))
                }
            }
        )
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
                    primaryActionLabel = stringResource(id = R.string.home_start_game),
                    historyActionLabel = stringResource(id = R.string.home_history),
                    resumeActionLabel = if (uiState.isPaused) stringResource(id = R.string.home_resume) else null
                ),
                onStartGame = {
                    if (uiState.isPaused) showDiscardConfirmation = true else openPlayerSetup()
                },
                onOpenHistory = {
                    navController.navigate(AppRoutes.HISTORY) {
                        launchSingleTop = true
                    }
                },
                onResumeGame = {
                    gameViewModel.resumeGame()
                    navController.navigate(AppRoutes.GAMEPLAY) { launchSingleTop = true }
                }
            )
        }

        composable(AppRoutes.HISTORY) {
            LaunchedEffect(Unit) {
                historyViewModel.loadHistory()
            }

            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            val historyUiState = HistoryUiState(
                title = stringResource(id = R.string.history_title),
                backLabel = stringResource(id = R.string.history_back),
                loadingLabel = stringResource(id = R.string.history_loading),
                emptyLabel = stringResource(id = R.string.history_empty_state),
                isLoading = historyState.isLoading,
                games = historyState.games.map { game ->
                    val headline = if (game.isTie) {
                        stringResource(id = R.string.history_tie)
                    } else {
                        stringResource(
                            id = R.string.history_winner,
                            game.winnerName ?: "-"
                        )
                    }
                    val details = stringResource(
                        id = R.string.history_players_rounds,
                        game.totalPlayers,
                        game.totalRounds,
                        dateFormat.format(Date(game.completedAtEpochMs))
                    )
                    HistoryGameItemUiState(
                        sessionId = game.sessionId,
                        title = headline,
                        subtitle = details,
                        isSelected = game.sessionId == historyState.selectedSessionId
                    )
                },
                selectedResults = historyState.selectedResults.map { result ->
                    HistoryPlayerItemUiState(
                        rowLabel = stringResource(
                            id = R.string.history_result_row,
                            result.placement,
                            result.playerName,
                            result.score
                        )
                    )
                }
            )

            HistoryScreen(
                state = historyUiState,
                onBack = { navController.popBackStack() },
                onSelectGame = { sessionId ->
                    historyViewModel.selectSession(sessionId)
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
                onRoundCountChanged = { roundCount = it },
                onTimerEnabledChanged = { timerEnabled = it },
                onStartGame = {
                    val selectedNames = playerNames.take(playerCount).mapIndexed { index, name ->
                        name.trim().ifBlank { defaultPlayerNames[index] }
                    }

                    if (selectedNames.isEmpty()) {
                        playerSetupError = minPlayersValidationError
                        return@PlayerSetupScreen
                    }

                    playerSetupError = null
                    gameViewModel.startGame(selectedNames, roundCount, timerEnabled)
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
            val pauseAndGoHome: () -> Unit = {
                gameViewModel.pauseGame()
                navController.navigate(AppRoutes.HOME) {
                    popUpTo(AppRoutes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
            }
            BackHandler(onBack = pauseAndGoHome)
            val activeSession = uiState.session
            val activeQuestion = uiState.currentQuestion
            if (activeSession == null || activeQuestion == null) {
                Text(text = stringResource(id = R.string.gameplay_empty_state))
            } else {
                val answerFeedback = when (uiState.answerFeedback) {
                    AnswerFeedback.CORRECT -> stringResource(id = R.string.gameplay_feedback_correct)
                    AnswerFeedback.WRONG -> stringResource(id = R.string.gameplay_feedback_wrong)
                    AnswerFeedback.TIMEOUT -> stringResource(id = R.string.gameplay_feedback_timeout)
                    null -> null
                }
                val isAnswerCorrect = when (uiState.answerFeedback) {
                    AnswerFeedback.CORRECT -> true
                    AnswerFeedback.WRONG -> false
                    AnswerFeedback.TIMEOUT -> false
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
                    timerLabel = uiState.remainingTimerMs?.let {
                        stringResource(id = R.string.gameplay_timer, ((it + 999L) / 1000L).toInt())
                    },
                    fiftyFiftyLabel = stringResource(id = R.string.gameplay_lifeline_fifty_fifty),
                    isFiftyFiftyAvailable = !uiState.answerLocked && uiState.advice == null &&
                        !activeSession.hasUsedLifeline(
                            playerId = activeSession.activePlayer.id,
                            lifelineType = LifelineType.FIFTY_FIFTY
                        ),
                    audienceLabel = stringResource(id = R.string.gameplay_lifeline_audience),
                    isAudienceAvailable = !uiState.answerLocked && uiState.advice == null &&
                        !activeSession.hasUsedLifeline(activeSession.activePlayer.id, LifelineType.AUDIENCE),
                    friendLabel = stringResource(id = R.string.gameplay_lifeline_friend),
                    isFriendAvailable = !uiState.answerLocked && uiState.advice == null &&
                        !activeSession.hasUsedLifeline(activeSession.activePlayer.id, LifelineType.PHONE_A_FRIEND),
                    advice = uiState.advice?.let { advice ->
                        val isAudience = advice.type == LifelineType.AUDIENCE
                        val suggested = activeQuestion.options.first { it.id == advice.suggestedOptionId }
                        LifelineAdviceUiState(
                            title = stringResource(
                                id = if (isAudience) R.string.gameplay_lifeline_audience else R.string.gameplay_lifeline_friend
                            ),
                            message = if (isAudience) null else stringResource(
                                id = R.string.gameplay_advice_friend,
                                suggested.id,
                                suggested.textSv,
                                advice.confidencePercent
                            ),
                            votes = if (isAudience) activeQuestion.options.map { option ->
                                AudienceVoteUiState(option.id, advice.audiencePercentages.getValue(option.id))
                            } else emptyList(),
                            continueLabel = stringResource(id = R.string.gameplay_advice_continue)
                        )
                    },
                    questionLabel = stringResource(
                        id = R.string.gameplay_question,
                        activeQuestion.textSv
                    ),
                    options = activeQuestion.options.map { option ->
                        val visibleByLifeline = uiState.remainingOptionIds?.contains(option.id) ?: true
                        GameplayOptionUiState(
                            id = option.id,
                            displayText = stringResource(
                                id = R.string.gameplay_option,
                                option.id,
                                option.textSv
                            ),
                            isEnabled = visibleByLifeline
                        )
                    },
                    answerFeedback = answerFeedback,
                    correctAnswerLabel = if (uiState.answerFeedback != null) {
                        stringResource(
                            id = R.string.gameplay_correct_answer,
                            activeQuestion.options.first { it.id == activeQuestion.correctOptionId }.textSv
                        )
                    } else null,
                    isAnswerCorrect = isAnswerCorrect,
                    answerLocked = uiState.answerLocked
                )

                GameplayScreen(
                    state = gameplayState,
                    onUseFiftyFifty = {
                        gameViewModel.useFiftyFifty()
                    },
                    onUseAudience = { gameViewModel.useAdvice(LifelineType.AUDIENCE) },
                    onUseFriend = { gameViewModel.useAdvice(LifelineType.PHONE_A_FRIEND) },
                    onDismissAdvice = { gameViewModel.dismissAdvice() },
                    onPause = pauseAndGoHome,
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
