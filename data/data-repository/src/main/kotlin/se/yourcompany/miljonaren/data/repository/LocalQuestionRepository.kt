package se.yourcompany.miljonaren.data.repository

import kotlin.random.Random
import se.yourcompany.miljonaren.data.local.content.BundledQuestionPack
import se.yourcompany.miljonaren.data.local.content.BundledQuestionPacks
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.model.QuestionTheme
import se.yourcompany.miljonaren.domain.usecase.QuestionRepository

interface QuestionUsageStore {
    /** Completed games, oldest first; each game contains pack-qualified question IDs in use order. */
    fun load(): List<List<String>>
    fun save(games: List<List<String>>)
}

class InMemoryQuestionUsageStore : QuestionUsageStore {
    private var games: List<List<String>> = emptyList()
    override fun load() = games
    override fun save(games: List<List<String>>) { this.games = games.map { it.toList() } }
}

class LocalQuestionRepository(
    allQuestions: List<Question>? = null,
    private val usageStore: QuestionUsageStore = InMemoryQuestionUsageStore(),
    private val random: Random = Random.Default,
    packs: List<BundledQuestionPack>? = null
) : QuestionRepository {
    private val questionPacks = packs ?: allQuestions?.let {
        listOf(BundledQuestionPack(QuestionTheme("general", "Blandat"), it))
    } ?: BundledQuestionPacks.load()
    private var activePack = questionPacks.firstOrNull { it.theme.id == "general" }
        ?: requireNotNull(questionPacks.firstOrNull()) { "At least one question pack is required" }
    private var recentGames: List<List<String>> = emptyList()
    private val currentGame = mutableListOf<String>()
    private val lastUsed = mutableMapOf<String, Long>()
    private val roundDifficulties = mutableMapOf<String, Difficulty>()
    private var sequence = 0L

    init {
        require(questionPacks.map { it.theme.id }.distinct().size == questionPacks.size) {
            "Question pack IDs must be unique"
        }
        startGame()
    }

    override fun getQuestionThemes(): List<QuestionTheme> = questionPacks.map { it.theme }

    override fun selectQuestionTheme(themeId: String) {
        activePack = requireNotNull(questionPacks.firstOrNull { it.theme.id == themeId }) {
            "Unknown question theme: $themeId"
        }
    }

    override fun startGame() {
        recentGames = usageStore.load().takeLast(10).map { game ->
            game.map { key -> if ("::" in key) key else "general::$key" }
        }
        currentGame.clear()
        lastUsed.clear()
        roundDifficulties.clear()
        sequence = 0L
        recentGames.flatten().forEach { lastUsed[it] = sequence++ }
    }

    override fun recordCompletedGame() {
        if (currentGame.isEmpty()) return
        recentGames = (recentGames + listOf(currentGame.toList())).takeLast(10)
        usageStore.save(recentGames)
        currentGame.clear()
    }

    override fun getNextUnusedQuestion(askedIds: Set<String>): Question? = select(askedIds, null)

    override fun getNextQuestion(session: GameSession): Question? = select(session.askedQuestionIds, session)

    private fun select(askedIds: Set<String>, session: GameSession?): Question? {
        val allQuestions = activePack.questions
        if (allQuestions.isEmpty()) return null
        val notAsked = allQuestions.filter { it.id !in askedIds }
        val recentIds = recentGames.flatten().toSet()
        val fresh = notAsked.filter { usageKey(it.id) !in recentIds }
        val question = if (fresh.isNotEmpty()) {
            val balanced = if (session == null) fresh else balancedPool(fresh, session)
            balanced[random.nextInt(balanced.size)]
        } else {
            val reusable = notAsked.ifEmpty { allQuestions }
            val hardest = reusable.maxOf { it.difficulty.ordinal }
            reusable.filter { it.difficulty.ordinal == hardest }
                .minByOrNull { lastUsed[usageKey(it.id)] ?: Long.MIN_VALUE }!!
        }
        val key = usageKey(question.id)
        lastUsed[key] = sequence++
        currentGame += key
        return question
    }

    private fun usageKey(questionId: String): String = "${activePack.theme.id}::$questionId"

    private fun balancedPool(fresh: List<Question>, session: GameSession): List<Question> {
        val tie = session.tieBreak
        val key = if (tie == null) "round:${session.currentRound}" else "tie:${tie.round}"
        val difficulty = roundDifficulties.getOrPut(key) {
            val needed = tie?.remainingPlayerIds?.size ?: session.players.size
            val groups = fresh.groupBy { it.difficulty }
            val sufficient = groups.filterValues { it.size >= needed }.keys.toList()
            val candidates = sufficient.ifEmpty {
                val maximum = groups.values.maxOf { it.size }
                groups.filterValues { it.size == maximum }.keys.toList()
            }
            candidates[random.nextInt(candidates.size)]
        }
        return fresh.filter { it.difficulty == difficulty }.ifEmpty { fresh }
    }
}
