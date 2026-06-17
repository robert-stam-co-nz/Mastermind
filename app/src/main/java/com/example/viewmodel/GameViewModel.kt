package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.LeaderboardEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameDifficulty(val displayName: String, val codeLength: Int, val maxColors: Int, val maxAttempts: Int) {
    EASY("Easy", 4, 6, 12),
    MEDIUM("Medium", 4, 8, 10),
    HARD("Hard", 5, 8, 10)
}

enum class GameState {
    PLAYING,
    WON,
    LOST,
    SETUP
}

enum class GameTheme(val displayName: String) {
    BENTO_GRID("Bento Grid"),
    NEON_GALACTIC("Neon Galactic"),
    SUNSET_BOULEVARD("Sunset Boulevard"),
    RETRO_ARCADE("Retro Arcade"),
    PASTEL_CAROUSEL("Pastel Carousel")
}

data class MastermindGuess(
    val colors: List<Int>,
    val exactMatches: Int,
    val colorMatches: Int,
    val isRevealedAnimate: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = GameRepository(db.leaderboardDao())
    private val soundManager = SoundManager()

    // Sound toggle state
    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    // 2D/3D selection state (true is 3D mode, false is 2D mode)
    private val _is3DEnabled = MutableStateFlow(true)
    val is3DEnabled: StateFlow<Boolean> = _is3DEnabled.asStateFlow()

    // Active theme state
    private val _currentTheme = MutableStateFlow(GameTheme.BENTO_GRID)
    val currentTheme: StateFlow<GameTheme> = _currentTheme.asStateFlow()

    // Active difficulty state
    private val _difficulty = MutableStateFlow(GameDifficulty.EASY)
    val difficulty: StateFlow<GameDifficulty> = _difficulty.asStateFlow()

    // Share results preference (true means ask, false means don't ask)
    private val _shouldShowSharePrompt = MutableStateFlow(true)
    val shouldShowSharePrompt: StateFlow<Boolean> = _shouldShowSharePrompt.asStateFlow()

    // Current Game State
    private val _gameState = MutableStateFlow(GameState.SETUP)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Secret code to break
    private val _secretCode = MutableStateFlow<List<Int>>(emptyList())
    val secretCode: StateFlow<List<Int>> = _secretCode.asStateFlow()

    // Current active draft guess
    private val _currentGuess = MutableStateFlow<List<Int?>>(emptyList())
    val currentGuess: StateFlow<List<Int?>> = _currentGuess.asStateFlow()

    // Active cursor index (which peg hole we are editing, null means automatically place in next empty slot)
    private val _activeCursorIndex = MutableStateFlow<Int?>(0)
    val activeCursorIndex: StateFlow<Int?> = _activeCursorIndex.asStateFlow()

    // Guesses made so far
    private val _pastGuesses = MutableStateFlow<List<MastermindGuess>>(emptyList())
    val pastGuesses: StateFlow<List<MastermindGuess>> = _pastGuesses.asStateFlow()

    // Timer Duration in seconds
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime.asStateFlow()

    // Local Leaderboard results (filtered by current difficulty)
    val topScores: StateFlow<List<LeaderboardEntry>> = _difficulty
        .flatMapLatest { diff -> repository.getTopScores(diff.displayName) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    init {
        startNewGame()
    }

    fun setDifficulty(newDifficulty: GameDifficulty) {
        if (_difficulty.value != newDifficulty || _gameState.value == GameState.SETUP) {
            _difficulty.value = newDifficulty
            startNewGame()
        }
    }

    fun setTheme(theme: GameTheme) {
        _currentTheme.value = theme
        if (_soundEnabled.value) {
            soundManager.playSelection()
        }
    }

    fun toggleSound() {
        _soundEnabled.value = !_soundEnabled.value
        if (_soundEnabled.value) {
            soundManager.playTap()
        }
    }

    fun toggle2D3D() {
        _is3DEnabled.value = !_is3DEnabled.value
        if (_soundEnabled.value) {
            soundManager.playTap()
        }
    }

    fun set3DMode(enabled: Boolean) {
        if (_is3DEnabled.value != enabled) {
            _is3DEnabled.value = enabled
            if (_soundEnabled.value) {
                soundManager.playSelection()
            }
        }
    }

    fun setSharePromptEnabled(enabled: Boolean) {
        _shouldShowSharePrompt.value = enabled
    }

    fun selectPegSquare(index: Int) {
        _activeCursorIndex.value = index
        if (_soundEnabled.value) {
            soundManager.playSelection()
        }
    }

    fun selectColorIndex(colorIdx: Int) {
        val current = _currentGuess.value.toMutableList()
        val length = _difficulty.value.codeLength
        
        val cursor = _activeCursorIndex.value
        if (cursor != null && cursor < length) {
            current[cursor] = colorIdx
            _currentGuess.value = current
            // Auto increment to next null or next spot
            val nextEmpty = current.indices.firstOrNull { idx -> current[idx] == null }
            if (nextEmpty != null) {
                _activeCursorIndex.value = nextEmpty
            } else {
                _activeCursorIndex.value = (cursor + 1) % length
            }
        } else {
            // Find first empty space
            val firstEmpty = current.indexOfFirst { it == null }
            if (firstEmpty != -1) {
                current[firstEmpty] = colorIdx
                _currentGuess.value = current
                
                val nextEmpty = current.indexOfFirst { it == null }
                _activeCursorIndex.value = if (nextEmpty != -1) nextEmpty else 0
            }
        }
        
        if (_soundEnabled.value) {
            soundManager.playTap()
        }
    }

    fun clearActiveGuessPosition(index: Int) {
        val current = _currentGuess.value.toMutableList()
        if (index < current.size) {
            current[index] = null
            _currentGuess.value = current
            _activeCursorIndex.value = index
        }
        if (_soundEnabled.value) {
            soundManager.playSelection()
        }
    }

    fun clearAllCurrentGuess() {
        val length = _difficulty.value.codeLength
        _currentGuess.value = List(length) { null }
        _activeCursorIndex.value = 0
        if (_soundEnabled.value) {
            soundManager.playSelection()
        }
    }

    fun submitGuess() {
        val currentDraft = _currentGuess.value
        val secret = _secretCode.value
        val totalLength = _difficulty.value.codeLength
        
        if (currentDraft.any { it == null }) {
            if (_soundEnabled.value) {
                soundManager.playError()
            }
            return
        }

        val completedGuessColors = currentDraft.filterNotNull()
        val (exact, colorMatch) = evaluateGuess(completedGuessColors, secret)

        val masterGuess = MastermindGuess(
            colors = completedGuessColors,
            exactMatches = exact,
            colorMatches = colorMatch,
            isRevealedAnimate = true
        )

        // Append to past guesses and clear draft
        val newPastGuesses = _pastGuesses.value + masterGuess
        _pastGuesses.value = newPastGuesses

        if (_soundEnabled.value) {
            soundManager.playMatch(exact > 0)
        }

        // Check Win/Loss conditions
        if (exact == totalLength) {
            _gameState.value = GameState.WON
            stopTimer()
            if (_soundEnabled.value) {
                soundManager.playWin()
            }
        } else if (newPastGuesses.size >= _difficulty.value.maxAttempts) {
            _gameState.value = GameState.LOST
            stopTimer()
            if (_soundEnabled.value) {
                soundManager.playLose()
            }
        } else {
            // Setup for next attempt
            clearAllCurrentGuess()
        }
    }

    fun startNewGame() {
        stopTimer()
        val diff = _difficulty.value
        _secretCode.value = List(diff.codeLength) { Random.nextInt(0, diff.maxColors) }
        _currentGuess.value = List(diff.codeLength) { null }
        _pastGuesses.value = emptyList()
        _gameState.value = GameState.PLAYING
        _activeCursorIndex.value = 0
        _elapsedTime.value = 0
        startTimer()
        
        if (_soundEnabled.value) {
            soundManager.playTap()
        }
    }

    fun saveScore(playerName: String) {
        val nameTrimmed = playerName.trim().ifEmpty { "Anonymous" }
        viewModelScope.launch {
            repository.insertScore(
                LeaderboardEntry(
                    playerName = nameTrimmed,
                    difficulty = _difficulty.value.displayName,
                    timeInSeconds = _elapsedTime.value,
                    attempts = _pastGuesses.value.size
                )
            )
        }
    }

    fun clearLedger() {
        viewModelScope.launch {
            repository.clearLeaderboard()
        }
    }

    private fun evaluateGuess(guess: List<Int>, secret: List<Int>): Pair<Int, Int> {
        var exact = 0
        val secretRemaining = mutableListOf<Int>()
        val guessRemaining = mutableListOf<Int>()
        
        for (i in secret.indices) {
            if (secret[i] == guess[i]) {
                exact++
            } else {
                secretRemaining.add(secret[i])
                guessRemaining.add(guess[i])
            }
        }
        
        var colorMatch = 0
        for (g in guessRemaining) {
            if (secretRemaining.contains(g)) {
                colorMatch++
                secretRemaining.remove(g) // avoid double matches
            }
        }
        
        return Pair(exact, colorMatch)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedTime.value += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

class GameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
