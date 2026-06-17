package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.platform.LocalContext
import com.example.viewmodel.GameTheme
import com.example.viewmodel.GameDifficulty
import com.example.viewmodel.GameState
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.MastermindGuess

enum class GameTab {
    PLAY, SETTINGS
}

// Color definition containers for themes
data class Palette(
    val bgStart: Color,
    val bgEnd: Color,
    val surfaceColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentColor: Color,
    val pegWebColors: List<Color>,
    val pegLabels: List<String>
)

val BentoGridPalette = Palette(
    bgStart = Color(0xFF1C1B1F),
    bgEnd = Color(0xFF121114),
    surfaceColor = Color(0xFF2B2930),
    textPrimary = Color(0xFFD0BCFF),
    textSecondary = Color(0xFF938F99),
    accentColor = Color(0xFFFF897E), // Salmon pink accent
    pegWebColors = listOf(
        Color(0xFFEF5350), // Red
        Color(0xFF42A5F5), // Blue
        Color(0xFF66BB6A), // Green
        Color(0xFFFFEE58), // Yellow
        Color(0xFFFFA726), // Orange
        Color(0xFFEC407A), // Pink
        Color(0xFFAB47BC), // Purple
        Color(0xFF26A69A)  // Teal
    ),
    pegLabels = listOf("R", "B", "G", "Y", "O", "P", "U", "T")
)

val NeonGalacticPalette = Palette(
    bgStart = Color(0xFF090A1A),
    bgEnd = Color(0xFF12142E),
    surfaceColor = Color(0x991E224D),
    textPrimary = Color(0xFF00FFFF),
    textSecondary = Color(0xFFB19FFB),
    accentColor = Color(0xFFFF1493),
    pegWebColors = listOf(
        Color(0xFFFF3366), // Red
        Color(0xFF33FF66), // Green
        Color(0xFF3366FF), // Blue
        Color(0xFFFFEE33), // Yellow
        Color(0xFFFF9933), // Orange
        Color(0xFFB333FF), // Purple
        Color(0xFF33FFEE), // Teal
        Color(0xFFFF33EE)  // Magenta
    ),
    pegLabels = listOf("R", "G", "B", "Y", "O", "P", "T", "M")
)

val SunsetBoulevardPalette = Palette(
    bgStart = Color(0xFF1E0D1C),
    bgEnd = Color(0xFF33162C),
    surfaceColor = Color(0xBB4A1E3D),
    textPrimary = Color(0xFFFFD700),
    textSecondary = Color(0xFFFF8C94),
    accentColor = Color(0xFFFF4500),
    pegWebColors = listOf(
        Color(0xFFFF1744), // Crimson
        Color(0xFFFF7043), // Coral
        Color(0xFFFFA726), // Orange
        Color(0xFFFFD54F), // Gold
        Color(0xFFFFAB91), // Peach
        Color(0xFFEC407A), // Rose
        Color(0xFFBA68C8), // Orchid
        Color(0xFF8E24AA)  // Plum
    ),
    pegLabels = listOf("C", "R", "O", "G", "A", "E", "I", "P")
)

val RetroArcadePalette = Palette(
    bgStart = Color(0xFF000000),
    bgEnd = Color(0xFF151515),
    surfaceColor = Color(0xFF222222),
    textPrimary = Color(0xFF39FF14), // CRT Green
    textSecondary = Color(0xFFFFE600),
    accentColor = Color(0xFF00E5FF),
    pegWebColors = listOf(
        Color(0xFFE53935), // Retro Red
        Color(0xFF43A047), // Retro Green
        Color(0xFF1E88E5), // Retro Blue
        Color(0xFFFDD835), // Retro Yellow
        Color(0xFFFB8C00), // Retro Orange
        Color(0xFF8E24AA), // Retro Purple
        Color(0xFF00ACC1), // Retro Cyan
        Color(0xFFD81B60)  // Retro Pink
    ),
    pegLabels = listOf("1", "2", "3", "4", "5", "6", "7", "8")
)

val PastelCarouselPalette = Palette(
    bgStart = Color(0xFFF6F3FF),
    bgEnd = Color(0xFFFFF0F5),
    surfaceColor = Color(0xEEFFFFFF),
    textPrimary = Color(0xFF492F7A),
    textSecondary = Color(0xFF90507F),
    accentColor = Color(0xFFFF8D80),
    pegWebColors = listOf(
        Color(0xFFB2F2BB), // Mint
        Color(0xFFD0EBFF), // Lavender
        Color(0xFFFFD8A8), // Peach
        Color(0xFFFFF3BF), // Buttercup
        Color(0xFFFFD3E2), // Candy Pink
        Color(0xFFA5F3FC), // Seafoam
        Color(0xFFE5DBFF), // Lilac
        Color(0xFFFFC9C9)  // Salmon
    ),
    pegLabels = listOf("M", "L", "E", "B", "C", "S", "I", "N")
)

val LightModernPalette = Palette(
    bgStart = Color(0xFFFFFFFF),
    bgEnd = Color(0xFFF5F7FA),
    surfaceColor = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF666666),
    accentColor = Color(0xFF007AFF),
    pegWebColors = listOf(
        Color(0xFFEF5350), // Red
        Color(0xFF42A5F5), // Blue
        Color(0xFF66BB6A), // Green
        Color(0xFFFFEE58), // Yellow
        Color(0xFFFFA726), // Orange
        Color(0xFFEC407A), // Pink
        Color(0xFFAB47BC), // Purple
        Color(0xFF26A69A)  // Teal
    ),
    pegLabels = listOf("R", "B", "G", "Y", "O", "P", "U", "T")
)

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val currentGuess by viewModel.currentGuess.collectAsStateWithLifecycle()
    val activeCursorIndex by viewModel.activeCursorIndex.collectAsStateWithLifecycle()
    val pastGuesses by viewModel.pastGuesses.collectAsStateWithLifecycle()
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val secretCode by viewModel.secretCode.collectAsStateWithLifecycle()
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val elapsedTime by viewModel.elapsedTime.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val is3DEnabled by viewModel.is3DEnabled.collectAsStateWithLifecycle()
    val shouldShowSharePrompt by viewModel.shouldShowSharePrompt.collectAsStateWithLifecycle()

    GameScreenContent(
        currentGuess = currentGuess,
        activeCursorIndex = activeCursorIndex,
        pastGuesses = pastGuesses,
        gameState = gameState,
        secretCode = secretCode,
        difficulty = difficulty,
        elapsedTime = elapsedTime,
        soundEnabled = soundEnabled,
        is3DEnabled = is3DEnabled,
        shouldShowSharePrompt = shouldShowSharePrompt,
        onToggleSound = { viewModel.toggleSound() },
        onStartNewGame = { viewModel.startNewGame() },
        onSelectColorIndex = { viewModel.selectColorIndex(it) },
        onClearAllCurrentGuess = { viewModel.clearAllCurrentGuess() },
        onSetDifficulty = { viewModel.setDifficulty(it) },
        onSet3DMode = { viewModel.set3DMode(it) },
        onSetSharePromptEnabled = { viewModel.setSharePromptEnabled(it) },
        onSelectPegSquare = { viewModel.selectPegSquare(it) },
        onSubmitGuess = { viewModel.submitGuess() }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameScreenContent(
    currentGuess: List<Int?>,
    activeCursorIndex: Int?,
    pastGuesses: List<MastermindGuess>,
    gameState: GameState,
    secretCode: List<Int>,
    difficulty: GameDifficulty,
    elapsedTime: Int,
    soundEnabled: Boolean,
    is3DEnabled: Boolean,
    shouldShowSharePrompt: Boolean,
    onToggleSound: () -> Unit,
    onStartNewGame: () -> Unit,
    onSelectColorIndex: (Int) -> Unit,
    onClearAllCurrentGuess: () -> Unit,
    onSetDifficulty: (GameDifficulty) -> Unit,
    onSet3DMode: (Boolean) -> Unit,
    onSetSharePromptEnabled: (Boolean) -> Unit,
    onSelectPegSquare: (Int) -> Unit,
    onSubmitGuess: () -> Unit
) {
    val palette = LightModernPalette
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val playListState = rememberLazyListState()

    var currentTab by remember { mutableStateOf(GameTab.PLAY) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameState) {
        if ((gameState == GameState.WON || gameState == GameState.LOST) && shouldShowSharePrompt) {
            showShareDialog = true
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = palette.surfaceColor,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == GameTab.PLAY,
                    onClick = { currentTab = GameTab.PLAY },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                    label = { Text("Play", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.textPrimary,
                        selectedTextColor = palette.textPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary,
                        indicatorColor = palette.textPrimary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == GameTab.SETTINGS,
                    onClick = { currentTab = GameTab.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.textPrimary,
                        selectedTextColor = palette.textPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary,
                        indicatorColor = palette.textPrimary.copy(alpha = 0.15f)
                    )
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.bgStart, palette.bgEnd)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentTab == GameTab.PLAY) {
                PlayTabContent(
                    palette = palette,
                    currentGuess = currentGuess,
                    activeCursorIndex = activeCursorIndex,
                    pastGuesses = pastGuesses,
                    gameState = gameState,
                    secretCode = secretCode,
                    difficulty = difficulty,
                    elapsedTime = elapsedTime,
                    soundEnabled = soundEnabled,
                    is3DEnabled = is3DEnabled,
                    listState = playListState,
                    graphicsLayer = graphicsLayer,
                    onShowInfo = { showInfoDialog = true },
                    onToggleSound = onToggleSound,
                    onStartNewGame = onStartNewGame,
                    onSelectColorIndex = onSelectColorIndex,
                    onClearAllCurrentGuess = onClearAllCurrentGuess,
                    onSelectPegSquare = onSelectPegSquare,
                    onSubmitGuess = onSubmitGuess
                )
            } else {
                SettingsTabContent(
                    palette = palette,
                    difficulty = difficulty,
                    is3DEnabled = is3DEnabled,
                    shouldShowSharePrompt = shouldShowSharePrompt,
                    onSetDifficulty = onSetDifficulty,
                    onSet3DMode = onSet3DMode,
                    onSetSharePromptEnabled = onSetSharePromptEnabled
                )
            }

            // Help info overlay dialog
            if (showInfoDialog) {
                InstructionsDialog(palette = palette) { showInfoDialog = false }
            }

            // Share results overlay dialog
            if (showShareDialog) {
                ShareResultsDialog(
                    palette = palette,
                    difficulty = difficulty,
                    linesUsed = pastGuesses.size,
                    gameState = gameState,
                    onShare = {
                        coroutineScope.launch {
                            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                            val uri = saveBitmapToCache(context, bitmap)
                            val shareText = "I just played Mastermind on ${difficulty.displayName} mode and used ${pastGuesses.size} lines! Can you beat my sequence cracking skills?"
                            
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "image/png"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share your sequence cracking skills")
                            context.startActivity(shareIntent)
                            showShareDialog = false
                        }
                    },
                    onDontAskAgain = {
                        onSetSharePromptEnabled(false)
                        showShareDialog = false
                    },
                    onDismiss = { showShareDialog = false }
                )
            }
        }
    }
}

private fun saveBitmapToCache(context: android.content.Context, bitmap: Bitmap): Uri {
    val imagesFolder = File(context.cacheDir, "images")
    imagesFolder.mkdirs()
    val file = File(imagesFolder, "share_image.png")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.flush()
    stream.close()
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800, apiLevel = 36)
@Composable
fun GameScreenPreview() {
    GameScreenContent(
        currentGuess = listOf(0, 1, null, null),
        activeCursorIndex = 2,
        pastGuesses = listOf(
            MastermindGuess(listOf(1, 2, 3, 4), 1, 2)
        ),
        gameState = GameState.PLAYING,
        secretCode = listOf(0, 1, 2, 3),
        difficulty = GameDifficulty.EASY,
        elapsedTime = 45,
        soundEnabled = true,
        is3DEnabled = false,
        shouldShowSharePrompt = true,
        onToggleSound = {},
        onStartNewGame = {},
        onSelectColorIndex = {},
        onClearAllCurrentGuess = {},
        onSetDifficulty = {},
        onSet3DMode = {},
        onSetSharePromptEnabled = {},
        onSelectPegSquare = {},
        onSubmitGuess = {}
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayTabContent(
    palette: Palette,
    currentGuess: List<Int?>,
    activeCursorIndex: Int?,
    pastGuesses: List<MastermindGuess>,
    gameState: GameState,
    secretCode: List<Int>,
    difficulty: GameDifficulty,
    elapsedTime: Int,
    soundEnabled: Boolean,
    is3DEnabled: Boolean,
    listState: LazyListState,
    graphicsLayer: androidx.compose.ui.graphics.layer.GraphicsLayer,
    onShowInfo: () -> Unit,
    onToggleSound: () -> Unit,
    onStartNewGame: () -> Unit,
    onSelectColorIndex: (Int) -> Unit,
    onClearAllCurrentGuess: () -> Unit,
    onSelectPegSquare: (Int) -> Unit,
    onSubmitGuess: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = configuration.screenWidthDp >= 600

    val pegSize = when {
        isLargeScreen -> 44.dp
        isLandscape -> 32.dp
        difficulty.maxColors > 6 -> 32.dp
        else -> 38.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Play Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "MASTERMIND",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = palette.textPrimary
                )
                Text(
                    text = "Mode: ${difficulty.displayName} • Crack the sequence",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("sound_toggle")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = palette.textPrimary
                    )
                }

                IconButton(
                    onClick = onShowInfo,
                    modifier = Modifier.testTag("rules_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Game Rules",
                        tint = palette.textPrimary
                    )
                }

                IconButton(
                    onClick = onStartNewGame,
                    modifier = Modifier.testTag("restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Game",
                        tint = palette.textPrimary
                    )
                }
            }
        }

        // Stats Display Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val remainingAttempts = difficulty.maxAttempts - pastGuesses.size
            Text(
                text = "Attempts Left: $remainingAttempts / ${difficulty.maxAttempts}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (remainingAttempts <= 3) palette.accentColor else palette.textPrimary
            )

            Box(
                modifier = Modifier
                    .background(palette.surfaceColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${elapsedTime}s",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = palette.textPrimary
                    )
                }
            }
        }

        // Expanded Board to receive maximum UI
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawContent()
                }
        ) {
            PlayGameBoard(
                pastGuesses = pastGuesses,
                currentGuess = currentGuess,
                activeCursorIndex = activeCursorIndex,
                gameState = gameState,
                difficulty = difficulty,
                palette = palette,
                listState = listState,
                is3DEnabled = is3DEnabled,
                onSelectPegSquare = onSelectPegSquare,
                onSubmitGuess = onSubmitGuess
            )
        }

        // Game Over Secret Reveal Banner (if lost)
        if (gameState == GameState.LOST) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("secret_reveal_card"),
                colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, palette.accentColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DECODER LOCKED - SECRET REVEALED",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = palette.accentColor,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        secretCode.forEach { colIdx ->
                            val pegColor = palette.pegWebColors.getOrElse(colIdx) { Color.Gray }
                            val label = palette.pegLabels.getOrElse(colIdx) { "?" }
                            ThreeDPeg(
                                color = pegColor,
                                label = label,
                                size = 36.dp,
                                is3D = is3DEnabled
                            )
                        }
                    }

                    Button(
                        onClick = onStartNewGame,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.accentColor),
                        modifier = Modifier.testTag("try_again_button")
                    ) {
                        Text("TRY AGAIN", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Color Selection Palette at bottom
        if (gameState == GameState.PLAYING) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHOOSE COLOR PEG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = palette.textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Peg option choices and control buttons with dynamic FlowRow wrapping
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            for (i in 0 until difficulty.maxColors) {
                                val col = palette.pegWebColors.getOrNull(i) ?: Color.Gray
                                val label = palette.pegLabels.getOrNull(i) ?: "?"

                                ThreeDPeg(
                                    color = col,
                                    label = label,
                                    size = pegSize,
                                    is3D = is3DEnabled,
                                    modifier = Modifier
                                        .clickable { onSelectColorIndex(i) }
                                        .testTag("palette_choice_$i")
                                )
                            }
                        }

                        // Clear Row Button
                        IconButton(
                            onClick = onClearAllCurrentGuess,
                            modifier = Modifier.testTag("erase_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Draft",
                                tint = palette.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTabContent(
    palette: Palette,
    difficulty: GameDifficulty,
    is3DEnabled: Boolean,
    shouldShowSharePrompt: Boolean,
    onSetDifficulty: (GameDifficulty) -> Unit,
    onSet3DMode: (Boolean) -> Unit,
    onSetSharePromptEnabled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = palette.textPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 1. Difficulty levels card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECT DIFFICULTY LEVEL",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GameDifficulty.entries.forEach { diff ->
                        val isSelected = difficulty == diff
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) palette.accentColor.copy(alpha = 0.15f) else Color.Transparent
                                )
                                .border(
                                    1.2.dp,
                                    if (isSelected) palette.accentColor else palette.textSecondary.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSetDifficulty(diff) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = diff.displayName.uppercase(),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) palette.accentColor else palette.textPrimary
                                )
                                Text(
                                    text = "${diff.codeLength} Pegs • ${diff.maxColors} Colors • ${diff.maxAttempts} Tries",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active selection",
                                    tint = palette.accentColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1.5 Render style (2D vs 3D) selection card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECT BOARD STYLE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 2D
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!is3DEnabled) palette.accentColor.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .border(
                                1.2.dp,
                                if (!is3DEnabled) palette.accentColor else palette.textSecondary.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSet3DMode(false) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "2D FLAT",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (!is3DEnabled) palette.accentColor else palette.textPrimary
                            )
                            Text(
                                text = "Modern & flat",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textSecondary
                            )
                        }
                        if (!is3DEnabled) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active 2D",
                                tint = palette.accentColor
                            )
                        }
                    }

                    // Option 3D
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (is3DEnabled) palette.accentColor.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .border(
                                1.2.dp,
                                if (is3DEnabled) palette.accentColor else palette.textSecondary.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSet3DMode(true) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "3D GLASS",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (is3DEnabled) palette.accentColor else palette.textPrimary
                            )
                            Text(
                                text = "Gloss & float animation",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textSecondary
                            )
                        }
                        if (is3DEnabled) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active 3D",
                                tint = palette.accentColor
                            )
                        }
                    }
                }
            }
        }

        // 2. Share Results preference card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SHARE RESULTS PROMPT",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = palette.textPrimary
                        )
                        Text(
                            text = "Ask to share after game ends",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                    Switch(
                        checked = shouldShowSharePrompt,
                        onCheckedChange = onSetSharePromptEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.accentColor,
                            checkedTrackColor = palette.accentColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        // 3. Brief visual instructions
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📖 QUICK GUIDE Rules",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "• Tap empty sockets inside the active row on the Board to choose placement.\n" +
                           "• Tap color choice pegs at the bottom to insert a color.\n" +
                           "• Confirm Guess: Once the active row is fully drafted with peg pins, select the ✔ (Confirm) action directly inline with the row to submit for matching verification feedback:\n" +
                           "  - Green Circles: Correct color + correct position.\n" +
                           "  - Orange Circles: Correct color, but wrong slot placement.\n" +
                           "  - Hollow Outlined Circles: Color entirely absent.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = palette.textSecondary
                )
            }
        }
    }
}

@Composable
fun PlayGameBoard(
    pastGuesses: List<MastermindGuess>,
    currentGuess: List<Int?>,
    activeCursorIndex: Int?,
    gameState: GameState,
    difficulty: GameDifficulty,
    palette: Palette,
    listState: LazyListState,
    is3DEnabled: Boolean,
    onSelectPegSquare: (Int) -> Unit,
    onSubmitGuess: () -> Unit
) {
    // Scroll to the latest guess or active guess row, keeping previous entries in view
    LaunchedEffect(pastGuesses.size) {
        val targetIndex = pastGuesses.size
        if (targetIndex >= 6) {
            // Only scroll down when the player is on the last few lines of the board (index >= 6),
            // keeping previous lines and the first line visible as long as possible
            val scrollIndex = (targetIndex - 4).coerceAtLeast(0)
            listState.animateScrollToItem(scrollIndex)
        } else {
            // Otherwise, keep the first line (index 0) fully visible in focus
            listState.animateScrollToItem(0)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("history_card_board"),
        colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, palette.textSecondary.copy(alpha = 0.15f))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(difficulty.maxAttempts) { index ->
                if (index < pastGuesses.size) {
                    HistoryRowItem(
                        index = index,
                        guess = pastGuesses[index],
                        difficulty = difficulty,
                        palette = palette,
                        is3D = is3DEnabled
                    )
                } else if (index == pastGuesses.size && gameState == GameState.PLAYING) {
                    ActiveDraftRowItem(
                        index = index,
                        currentGuess = currentGuess,
                        activeCursorIndex = activeCursorIndex,
                        palette = palette,
                        is3D = is3DEnabled,
                        onSelectPegSquare = onSelectPegSquare,
                        onSubmitGuess = onSubmitGuess
                    )
                } else {
                    PlaceholderRowItem(
                        index = index,
                        difficulty = difficulty,
                        palette = palette,
                        is3D = is3DEnabled
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryRowItem(
    index: Int,
    guess: MastermindGuess,
    difficulty: GameDifficulty,
    palette: Palette,
    is3D: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surfaceColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Row number
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(palette.textPrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Guest Peg symbols
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                guess.colors.forEach { colIdx ->
                    val pegColor = palette.pegWebColors.getOrElse(colIdx) { Color.Gray }
                    val label = palette.pegLabels.getOrElse(colIdx) { "?" }
                    ThreeDPeg(
                        color = pegColor,
                        label = label,
                        size = 36.dp,
                        is3D = is3D
                    )
                }
            }
        }

        // Result hints alignment on the right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            // Exact Matches (Green dots - Correct color + position)
            repeat(guess.exactMatches) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF4CAF50), CircleShape) // Green
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                )
            }

            // Color Matches (Orange dots - Correct color, wrong position)
            repeat(guess.colorMatches) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFFF9800), CircleShape) // Orange
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                )
            }

            // Remaining empty feedback slots
            val emptySlots = difficulty.codeLength - (guess.exactMatches + guess.colorMatches)
            repeat(emptySlots.coerceAtLeast(0)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .border(1.dp, palette.textSecondary.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun ActiveDraftRowItem(
    index: Int,
    currentGuess: List<Int?>,
    activeCursorIndex: Int?,
    palette: Palette,
    is3D: Boolean = true,
    onSelectPegSquare: (Int) -> Unit,
    onSubmitGuess: () -> Unit
) {
    val draftFilled = currentGuess.all { it != null }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.textPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.2.dp, palette.textPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Glowing Active Row Index Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(palette.textPrimary, CircleShape)
                    .shadow(1.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.surfaceColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Draft pegs representing direct board input
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                currentGuess.forEachIndexed { colIdx, valIdx ->
                    val isFocused = activeCursorIndex == colIdx
                    val col = valIdx?.let { palette.pegWebColors.getOrNull(it) } ?: Color.Transparent
                    val label = valIdx?.let { palette.pegLabels.getOrNull(it) } ?: " "

                    if (valIdx != null) {
                        ThreeDPeg(
                            color = col,
                            label = label,
                            size = 36.dp,
                            isSelected = isFocused,
                            is3D = is3D,
                            modifier = Modifier
                                .clickable { onSelectPegSquare(colIdx) }
                                .testTag("draft_hole_$colIdx")
                        )
                    } else {
                        ThreeDPegSocket(
                            size = 36.dp,
                            isFocused = isFocused,
                            focusedColor = palette.accentColor,
                            outlineColor = palette.textPrimary,
                            socketInnerColor = palette.bgEnd,
                            is3D = is3D,
                            modifier = Modifier
                                .clickable { onSelectPegSquare(colIdx) }
                                .testTag("draft_hole_$colIdx")
                        )
                    }
                }
            }
        }

        // Action inline: submit guess/checkmark
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            IconButton(
                onClick = { if (draftFilled) onSubmitGuess() },
                enabled = draftFilled,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (draftFilled) palette.accentColor else palette.surfaceColor.copy(alpha = 0.4f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm Guess",
                    tint = if (draftFilled) Color.White else palette.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PlaceholderRowItem(
    index: Int,
    difficulty: GameDifficulty,
    palette: Palette,
    is3D: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Index number
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, palette.textPrimary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textPrimary.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Inactive placeholder sockets representing physical board
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(difficulty.codeLength) {
                    ThreeDPegSocket(
                        size = 36.dp,
                        isFocused = false,
                        outlineColor = palette.textPrimary,
                        socketInnerColor = palette.bgEnd,
                        is3D = is3D
                    )
                }
            }
        }

        // Empty feedbacks dots placeholder on the right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            repeat(difficulty.codeLength) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .border(1.dp, palette.textSecondary.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}

fun Color.darken(factor: Float = 0.6f): Color {
    return Color(
        red = red * factor,
        green = green * factor,
        blue = blue * factor,
        alpha = alpha
    )
}

@Composable
fun ThreeDPeg(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    isSelected: Boolean = false,
    is3D: Boolean = true
) {
    if (!is3D) {
        val extraScale by animateFloatAsState(
            targetValue = if (isSelected) 1.15f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "scale"
        )
        Box(
            modifier = modifier
                .size(size)
                .graphicsLayer {
                    scaleX = extraScale
                    scaleY = extraScale
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .background(Color.Black.copy(alpha = 0.15f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isSelected) 0.dp else 1.5.dp)
                    .background(color, CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 1.5.dp,
                        color = if (isSelected) LightModernPalette.textPrimary else color.darken(0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = if (color.luminance() > 0.82f) Color.Black else Color.White
                )
            }
        }
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "peg_float")
        val floatOffset by if (isSelected) {
            infiniteTransition.animateFloat(
                initialValue = -2f,
                targetValue = 2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "float"
            )
        } else {
            remember { mutableFloatStateOf(0f) }
        }

        val extraScale by animateFloatAsState(
            targetValue = if (isSelected) 1.15f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "scale"
        )

        val shadowAlpha by animateFloatAsState(
            targetValue = if (isSelected) 0.4f else 0.25f,
            label = "shadowAlpha"
        )

        Box(
            modifier = modifier
                .size(size)
                .graphicsLayer {
                    scaleX = extraScale
                    scaleY = extraScale
                    translationY = floatOffset
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = this.size.width / 2f
                val shadowCenter = center.copy(y = center.y + (radius * 0.15f) + if (isSelected) 4f else 0f)
                drawCircle(
                    color = Color.Black.copy(alpha = shadowAlpha),
                    radius = radius * 0.95f,
                    center = shadowCenter
                )
            }

            Canvas(modifier = Modifier.fillMaxSize().padding(1.dp)) {
                val r = this.size.width / 2f
                val c = center

                drawCircle(
                    color = color.darken(0.4f),
                    radius = r,
                    center = c
                )

                val radialCenter = androidx.compose.ui.geometry.Offset(r * 0.7f, r * 0.7f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            color,
                            color.darken(0.5f)
                        ),
                        center = radialCenter,
                        radius = r * 1.2f
                    ),
                    radius = r - 1.5f,
                    center = c
                )

                val glareCenter = androidx.compose.ui.geometry.Offset(r * 0.55f, r * 0.55f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = glareCenter,
                        radius = r * 0.45f
                    ),
                    radius = r * 0.4f,
                    center = glareCenter
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                        blurRadius = 2f
                    )
                ),
                color = if (color.luminance() > 0.82f) Color.Black else Color.White
            )
        }
    }
}

@Composable
fun ThreeDPegSocket(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 46.dp,
    isFocused: Boolean = false,
    focusedColor: Color = BentoGridPalette.textPrimary,
    outlineColor: Color = BentoGridPalette.textPrimary.copy(alpha = 0.3f),
    socketInnerColor: Color = Color(0xFFE0E0E0),
    is3D: Boolean = true
) {
    if (!is3D) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(socketInnerColor.copy(alpha = 0.6f))
                .border(
                    width = if (isFocused) 3.dp else 1.5.dp,
                    color = outlineColor,
                    shape = CircleShape
                )
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = this.size.width / 2f
                val c = center

                drawCircle(
                    color = Color.Black.copy(alpha = 0.1f),
                    radius = r,
                    center = c
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            socketInnerColor,
                            socketInnerColor.copy(alpha = 0.3f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(r * 0.4f, r * 0.4f),
                        radius = r * 1.1f
                    ),
                    radius = r - 2f,
                    center = c
                )

                drawCircle(
                    color = outlineColor.copy(alpha = if (isFocused) 1f else 0.6f),
                    radius = r - 2f,
                    center = c,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = if (isFocused) 3.dp.toPx() else 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun InstructionsDialog(
    palette: Palette,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(1.5.dp, palette.textPrimary),
            modifier = Modifier.testTag("instructions_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "📖 MASTERMIND MANUAL",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Goal of the game:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )
                Text(
                    text = "The system automatically synthesizes a hidden pattern sequence of pegs. Your objective is to decipher it before running out of attempts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 12.dp, top = 2.dp)
                )

                Text(
                    text = "Decipher Feedback Symbols:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )
                Text(
                    text = "• Green Dots: Means a peg in your guess is correct in both color AND position.\n" +
                           "• Orange Dots: Means a peg in your guess is correct in color, but in the WRONG position.\n" +
                           "• Empty slots (Outline circles): Incorrect peg color that is completely absent from the secret key.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 12.dp, top = 2.dp)
                )

                Text(
                    text = "Accessible Design Support:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )
                Text(
                    text = "Our design supports accessible characters inside each colorful circle. Tapping on a draft hole lets you directly override its color.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 16.dp, top = 2.dp)
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = palette.accentColor),
                    modifier = Modifier.fillMaxWidth().testTag("close_instructions_button")
                ) {
                    Text("UNDERSTOOD", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ShareResultsDialog(
    palette: Palette,
    difficulty: GameDifficulty,
    linesUsed: Int,
    gameState: GameState,
    onShare: () -> Unit,
    onDontAskAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
            border = BorderStroke(2.dp, palette.textPrimary),
            modifier = Modifier.testTag("share_results_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (gameState == GameState.WON) "🎉 GAME WON!" else "🎮 GAME OVER",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Share your performance with friends?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = palette.accentColor),
                    modifier = Modifier.fillMaxWidth().testTag("share_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHARE RESULTS", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDontAskAgain,
                    modifier = Modifier.fillMaxWidth().testTag("dont_ask_button")
                ) {
                    Text("DON'T ASK AGAIN", color = palette.textSecondary, fontSize = 12.sp)
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_share_button")
                ) {
                    Text("NOT NOW", color = palette.textSecondary)
                }
            }
        }
    }
}
