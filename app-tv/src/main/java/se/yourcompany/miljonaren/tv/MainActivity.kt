package se.yourcompany.miljonaren.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.yourcompany.miljonaren.core.navigation.AppRoutes
import se.yourcompany.miljonaren.feature.gameplay.GameplayScreen
import se.yourcompany.miljonaren.feature.home.HomeScreen
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupScreen
import se.yourcompany.miljonaren.feature.results.ResultsScreen
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
                onStartGame = {
                    navController.navigate(AppRoutes.PLAYER_SETUP) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.PLAYER_SETUP) {
            PlayerSetupScreen(
                onStartGame = { playerNames ->
                    gameViewModel.startGame(playerNames)
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
                GameplayScreen(
                    activePlayer = activeSession.activePlayer,
                    currentRound = activeSession.currentRound,
                    maxRounds = activeSession.maxRounds,
                    question = activeQuestion,
                    answerFeedback = uiState.answerFeedback,
                    answerLocked = uiState.answerLocked,
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
                ResultsScreen(
                    result = gameResult,
                    onRestart = {
                        gameViewModel.restartGame()
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
