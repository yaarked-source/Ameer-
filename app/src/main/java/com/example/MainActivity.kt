package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.MiniMusicBar
import com.example.ui.components.YouTubePlayerDialog
import com.example.ui.screens.CombosScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MusicDashboardScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.theme.CombatBackground
import com.example.ui.theme.CombatBorder
import com.example.ui.theme.CombatSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TrainingViewModel

enum class NavigationDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Arena", Icons.Filled.Home, Icons.Outlined.Home, "nav_arena"),
    WORKOUT("Rounds", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_workout"),
    MUSIC("Songs", Icons.Filled.MusicNote, Icons.Outlined.MusicNote, "nav_music"),
    COMBOS("Combos", Icons.Filled.SportsMma, Icons.Outlined.SportsMma, "nav_combos"),
    STATS("Stats", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_stats")
}

class MainActivity : ComponentActivity() {

    private val trainingViewModel: TrainingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ShadowStrikeApp(viewModel = trainingViewModel)
            }
        }
    }
}

@Composable
fun ShadowStrikeApp(viewModel: TrainingViewModel) {
    var currentDestination by remember { mutableStateOf(NavigationDestination.HOME) }
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlayingMusic by viewModel.isPlayingMusic.collectAsState()
    val showPlayerDialog by viewModel.showPlayerDialog.collectAsState()

    // Handle back button to return to home if in secondary tab
    BackHandler(enabled = currentDestination != NavigationDestination.HOME) {
        currentDestination = NavigationDestination.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CombatBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(CombatSurface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Persistent Floating Mini Music Controller Bar
                if (currentSong != null) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        MiniMusicBar(
                            currentSong = currentSong,
                            isPlaying = isPlayingMusic,
                            onTogglePlay = { viewModel.toggleMusicPlay() },
                            onNextTrack = { viewModel.nextSong() },
                            onOpenPlayer = { viewModel.openPlayerDialog() }
                        )
                    }
                }

                NavigationBar(
                    containerColor = CombatSurface,
                    contentColor = CrimsonRed,
                    tonalElevation = 0.dp
                ) {
                    NavigationDestination.entries.forEach { dest ->
                        val isSelected = currentDestination == dest
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = dest },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = dest.title
                                )
                            },
                            label = { Text(dest.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonRed,
                                selectedTextColor = CrimsonRed,
                                indicatorColor = CrimsonRed.copy(alpha = 0.15f),
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavigationDestination.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToWorkout = { currentDestination = NavigationDestination.WORKOUT },
                        onNavigateToMusic = { currentDestination = NavigationDestination.MUSIC }
                    )
                }
                NavigationDestination.WORKOUT -> {
                    WorkoutScreen(viewModel = viewModel)
                }
                NavigationDestination.MUSIC -> {
                    MusicDashboardScreen(viewModel = viewModel)
                }
                NavigationDestination.COMBOS -> {
                    CombosScreen(
                        viewModel = viewModel,
                        onStartComboWorkout = { currentDestination = NavigationDestination.WORKOUT }
                    )
                }
                NavigationDestination.STATS -> {
                    StatsScreen(viewModel = viewModel)
                }
            }

            // Embedded YouTube Dialog
            if (showPlayerDialog && currentSong != null) {
                YouTubePlayerDialog(
                    song = currentSong!!,
                    onDismiss = { viewModel.closePlayerDialog() }
                )
            }
        }
    }
}
