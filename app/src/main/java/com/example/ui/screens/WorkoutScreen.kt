package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CombatRoundTimer
import com.example.ui.components.FighterCanvas
import com.example.ui.components.WorkoutPhase
import com.example.ui.theme.CombatBackground
import com.example.ui.theme.CombatBorder
import com.example.ui.theme.CombatSurface
import com.example.ui.theme.CombatSurfaceElevated
import com.example.ui.theme.CombatSurfaceHighlight
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TrainingViewModel

@Composable
fun WorkoutScreen(
    viewModel: TrainingViewModel,
    modifier: Modifier = Modifier
) {
    val currentRound by viewModel.currentRound.collectAsState()
    val totalRounds by viewModel.totalRounds.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val workSeconds by viewModel.workDurationSeconds.collectAsState()
    val restSeconds by viewModel.restDurationSeconds.collectAsState()
    val phase by viewModel.workoutPhase.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    val currentCombo by viewModel.currentCombo.collectAsState()
    val currentComboStep by viewModel.currentComboStep.collectAsState()
    val sessionPunches by viewModel.sessionPunches.collectAsState()
    val discipline by viewModel.sessionDiscipline.collectAsState()

    val currentTechnique by viewModel.currentTechnique.collectAsState()
    val strikeTriggerKey by viewModel.strikeTriggerKey.collectAsState()
    val isSouthpaw by viewModel.isSouthpaw.collectAsState()

    var showConfigDialog by remember { mutableStateOf(false) }

    val totalPhaseSeconds = if (phase == WorkoutPhase.REST) restSeconds else workSeconds
    val estimatedCalories = (sessionPunches / 8) + ((totalRounds * workSeconds - remainingSeconds) / 60 * 11).coerceAtLeast(0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CombatBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "COMBAT TRAINING SESSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = CrimsonRed
                    )
                    Text(
                        text = "$discipline Rounds",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { showConfigDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CombatSurfaceElevated)
                        .testTag("workout_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Workout Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Live Round Timer Component
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CombatSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CombatBorder)
            ) {
                CombatRoundTimer(
                    currentRound = currentRound,
                    totalRounds = totalRounds,
                    remainingSeconds = remainingSeconds,
                    totalPhaseSeconds = totalPhaseSeconds,
                    phase = phase,
                    isRunning = isTimerRunning,
                    onTogglePlayPause = { viewModel.toggleTimer() },
                    onSkipPhase = { viewModel.skipPhase() },
                    onReset = { viewModel.resetWorkout() },
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        // Combo Caller Display & Animated Guidance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsMma,
                                contentDescription = null,
                                tint = ElectricGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMBINATION CALLOUT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = ElectricGold
                            )
                        }

                        Text(
                            text = currentCombo.discipline,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentCombo.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Combo Steps Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentCombo.sequence.forEachIndexed { idx, tech ->
                            val isCurrent = idx == currentComboStep
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) CrimsonRed else CombatSurfaceHighlight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCurrent) CrimsonRed else CombatBorder
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = tech.code,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                        color = if (isCurrent) Color.White else ElectricGold
                                    )
                                    Text(
                                        text = tech.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (isCurrent) Color.White else TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tip: ${currentCombo.tips}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Live Animated Fighter Preview & Shadowboxer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F101A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CombatBorder)
            ) {
                FighterCanvas(
                    currentTechnique = currentTechnique,
                    strikeTriggerKey = strikeTriggerKey,
                    isSouthpaw = isSouthpaw,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Live Strike & Calorie Burn HUD
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Punches Thrown HUD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsMma,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$sessionPunches",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "Strikes Logged",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                // Estimated Calories Burned HUD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$estimatedCalories kcal",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "Calorie Burn",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Manual Punch Count Button
        item {
            Button(
                onClick = {
                    val tech = currentCombo.sequence.random()
                    viewModel.triggerStrike(tech)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("tap_strike_log_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.SportsMma, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Shadow Strike (+1)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Workout Configuration Dialog
    if (showConfigDialog) {
        var tempRounds by remember { mutableIntStateOf(totalRounds) }
        var tempWorkMinutes by remember { mutableIntStateOf(workSeconds / 60) }
        var tempRestSeconds by remember { mutableIntStateOf(restSeconds) }
        var tempDiscipline by remember { mutableStateOf(discipline) }

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Text("Configure Workout Rounds", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Discipline
                    Text("Fighting Discipline:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Boxing", "Muay Thai", "Kickboxing", "MMA").forEach { d ->
                            FilterChip(
                                selected = tempDiscipline == d,
                                onClick = { tempDiscipline = d },
                                label = { Text(d) }
                            )
                        }
                    }

                    // Rounds
                    Text("Total Rounds:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 5, 8, 10, 12).forEach { r ->
                            FilterChip(
                                selected = tempRounds == r,
                                onClick = { tempRounds = r },
                                label = { Text("$r Rds") }
                            )
                        }
                    }

                    // Work Duration
                    Text("Round Work Duration:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1 to "1 Min", 2 to "2 Min", 3 to "3 Min", 5 to "5 Min").forEach { (m, lbl) ->
                            FilterChip(
                                selected = tempWorkMinutes == m,
                                onClick = { tempWorkMinutes = m },
                                label = { Text(lbl) }
                            )
                        }
                    }

                    // Rest Duration
                    Text("Rest Between Rounds:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15 to "15s", 30 to "30s", 60 to "60s").forEach { (s, lbl) ->
                            FilterChip(
                                selected = tempRestSeconds == s,
                                onClick = { tempRestSeconds = s },
                                label = { Text(lbl) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setWorkoutSettings(
                            rounds = tempRounds,
                            workSeconds = tempWorkMinutes * 60,
                            restSeconds = tempRestSeconds,
                            discipline = tempDiscipline
                        )
                        showConfigDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text("Apply & Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
