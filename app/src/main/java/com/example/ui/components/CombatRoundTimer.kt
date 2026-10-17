package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CombatBorder
import com.example.ui.theme.CombatSurfaceElevated
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class WorkoutPhase {
    PREPARE,
    WORK,
    REST,
    COMPLETED
}

@Composable
fun CombatRoundTimer(
    currentRound: Int,
    totalRounds: Int,
    remainingSeconds: Int,
    totalPhaseSeconds: Int,
    phase: WorkoutPhase,
    isRunning: Boolean,
    onTogglePlayPause: () -> Unit,
    onSkipPhase: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalPhaseSeconds > 0) {
        (remainingSeconds.toFloat() / totalPhaseSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning && remainingSeconds <= 10 && remainingSeconds > 0) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val activeColor by animateColorAsState(
        targetValue = when (phase) {
            WorkoutPhase.WORK -> if (remainingSeconds <= 10) CrimsonRed else CrimsonRed
            WorkoutPhase.REST -> NeonCyan
            WorkoutPhase.PREPARE -> ElectricGold
            WorkoutPhase.COMPLETED -> NeonLime
        },
        label = "phase_color"
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Round Indicator Badges
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (r in 1..totalRounds.coerceAtMost(10)) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                r < currentRound -> CrimsonRed
                                r == currentRound -> if (phase == WorkoutPhase.WORK) CrimsonRed else ElectricGold
                                else -> CombatBorder
                            }
                        )
                )
            }
        }

        // Circular Timer Display
        Box(
            modifier = Modifier
                .size(220.dp)
                .clickable { onTogglePlayPause() }
                .testTag("round_timer_circle"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background track
                drawCircle(
                    color = CombatSurfaceElevated,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Glowing Sweep Progress Arc
                val sweepAngle = 360f * progress
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            activeColor.copy(alpha = 0.5f),
                            activeColor,
                            activeColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Inner ring ambient glow
                drawCircle(
                    color = activeColor.copy(alpha = 0.08f),
                    radius = radius - 16f,
                    center = center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Phase Header
                Text(
                    text = when (phase) {
                        WorkoutPhase.WORK -> "ROUND $currentRound / $totalRounds"
                        WorkoutPhase.REST -> "REST & BREATHE"
                        WorkoutPhase.PREPARE -> "GET READY"
                        WorkoutPhase.COMPLETED -> "WORKOUT FINISHED!"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = activeColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time digits
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status text
                Text(
                    text = if (isRunning) "ACTIVE" else "PAUSED",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isRunning) NeonLime else TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Control Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CombatSurfaceElevated)
                    .testTag("timer_reset_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    tint = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Surface(
                onClick = onTogglePlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .testTag("timer_play_pause_btn"),
                color = activeColor,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconButton(
                onClick = onSkipPhase,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CombatSurfaceElevated)
                    .testTag("timer_skip_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip Round",
                    tint = TextSecondary
                )
            }
        }
    }
}
