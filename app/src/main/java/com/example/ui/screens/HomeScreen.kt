package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CombatTechnique
import com.example.ui.components.FighterCanvas
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: TrainingViewModel,
    onNavigateToWorkout: () -> Unit,
    onNavigateToMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTechnique by viewModel.currentTechnique.collectAsState()
    val strikeTriggerKey by viewModel.strikeTriggerKey.collectAsState()
    val isSouthpaw by viewModel.isSouthpaw.collectAsState()
    val animSpeed by viewModel.animSpeed.collectAsState()
    val isAutoShadowboxing by viewModel.isAutoShadowboxing.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val sessionPunches by viewModel.sessionPunches.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CombatBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CombatBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_fighter_hero),
                        contentDescription = "Combat Training Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        CombatBackground.copy(alpha = 0.92f),
                                        CombatBackground.copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsMma,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHADOWSTRIKE COMBAT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                ),
                                color = CrimsonRed
                            )
                        }
                        Text(
                            text = "Home Fighting Arena",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Animated fighter kinematics & YouTube combat beats",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Live Animated Fighter Stage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("fighter_stage_card"),
                colors = CardDefaults.cardColors(containerColor = CombatSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CombatBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Stage Header & Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SHADOW FIGHTER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = ElectricGold
                            )
                            Text(
                                text = if (isSouthpaw) "Southpaw Stance" else "Orthodox Stance",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = isSouthpaw,
                                onClick = { viewModel.toggleSouthpaw() },
                                label = { Text(if (isSouthpaw) "Left" else "Right") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CrimsonRed,
                                    selectedLabelColor = Color.White
                                )
                            )

                            // Auto Routine Toggle
                            Surface(
                                onClick = { viewModel.toggleAutoShadowbox() },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAutoShadowboxing) CrimsonRed else CombatSurfaceHighlight,
                                modifier = Modifier.testTag("auto_shadowbox_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isAutoShadowboxing) Icons.Default.Stop else Icons.Default.Repeat,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAutoShadowboxing) "Stop" else "Auto Flow",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Animated Canvas Fighter
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F101A))
                    ) {
                        FighterCanvas(
                            currentTechnique = currentTechnique,
                            strikeTriggerKey = strikeTriggerKey,
                            isSouthpaw = isSouthpaw,
                            speedMultiplier = animSpeed,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speed Multiplier Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strike Cadence:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.85f to "Slow", 1.0f to "1.0x", 1.3f to "Fast", 1.6f to "Beast").forEach { (speed, label) ->
                                val isSelected = animSpeed == speed
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElectricGold else CombatSurfaceElevated)
                                        .clickable { viewModel.setAnimSpeed(speed) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Strike Control Pad
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TAP TO STRIKE & DRILL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 4
                ) {
                    val strikes = listOf(
                        CombatTechnique.JAB to NeonCyan,
                        CombatTechnique.CROSS to CrimsonRed,
                        CombatTechnique.LEAD_HOOK to ElectricGold,
                        CombatTechnique.REAR_HOOK to ElectricGold,
                        CombatTechnique.LEAD_UPPERCUT to CrimsonRed,
                        CombatTechnique.SLIP_LEFT to TextPrimary,
                        CombatTechnique.SLIP_RIGHT to TextPrimary,
                        CombatTechnique.ROLL_WEAVE to TextPrimary,
                        CombatTechnique.GUARD_BLOCK to NeonCyan,
                        CombatTechnique.MUAY_THAI_KNEE to NeonLime,
                        CombatTechnique.LEAD_BODY_KICK to NeonLime,
                        CombatTechnique.REAR_LOW_KICK to NeonLime
                    )

                    strikes.forEach { (tech, color) ->
                        StrikeKeyButton(
                            technique = tech,
                            accentColor = color,
                            onClick = { viewModel.triggerStrike(tech) }
                        )
                    }
                }
            }
        }

        // Quick Training Actions Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Round Timer Workout Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToWorkout() }
                        .testTag("start_workout_action_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = CrimsonRed)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Combat Rounds",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Round bell, combo caller, punch counter",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Songs Dashboard Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToMusic() }
                        .testTag("open_music_dashboard_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CombatSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ElectricGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = ElectricGold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Music Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Paste YouTube links, play hype fight tracks",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StrikeKeyButton(
    technique: CombatTechnique,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(76.dp)
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .testTag("strike_btn_${technique.code.lowercase()}"),
        color = CombatSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = technique.code,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                color = accentColor
            )
            Text(
                text = technique.displayName,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
