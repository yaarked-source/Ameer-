package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CombatTechnique
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class SparkParticle(
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val maxRadius: Float,
    var progress: Float = 0f
)

@Composable
fun FighterCanvas(
    currentTechnique: CombatTechnique?,
    strikeTriggerKey: Long, // Increment to re-trigger same strike
    modifier: Modifier = Modifier,
    isSouthpaw: Boolean = false,
    speedMultiplier: Float = 1.0f,
    onAnimationComplete: () -> Unit = {}
) {
    val strikeProgress = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<SparkParticle>() }
    var lastExecutedStrike by remember { mutableStateOf<CombatTechnique?>(null) }
    var activeCalloutText by remember { mutableStateOf("") }
    val calloutAlpha = remember { Animatable(0f) }

    // Idle boxer rhythm bounce transition
    val infiniteTransition = rememberInfiniteTransition(label = "boxer_bounce")
    val bouncePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bounce"
    )

    // Trigger strike animation whenever strikeTriggerKey changes or new technique is set
    LaunchedEffect(strikeTriggerKey, currentTechnique) {
        if (currentTechnique != null) {
            lastExecutedStrike = currentTechnique
            activeCalloutText = "${currentTechnique.displayName.uppercase()}!"

            // Animate strike snap: rapid extension and return
            strikeProgress.snapTo(0f)
            calloutAlpha.snapTo(1f)

            // Spawn dynamic sparks
            particles.clear()
            val baseColor = when (currentTechnique) {
                CombatTechnique.JAB -> NeonCyan
                CombatTechnique.CROSS -> CrimsonRed
                CombatTechnique.LEAD_HOOK, CombatTechnique.REAR_HOOK -> ElectricGold
                CombatTechnique.LEAD_UPPERCUT, CombatTechnique.REAR_UPPERCUT -> CrimsonRed
                CombatTechnique.MUAY_THAI_KNEE, CombatTechnique.LEAD_BODY_KICK, CombatTechnique.REAR_LOW_KICK -> NeonLime
                else -> ElectricGold
            }

            val numParticles = 14
            for (i in 0 until numParticles) {
                val angle = (2 * PI * i / numParticles).toFloat()
                val speed = (120..380).random().toFloat()
                particles.add(
                    SparkParticle(
                        startX = 0f,
                        startY = 0f,
                        vx = cos(angle) * speed,
                        vy = sin(angle) * speed,
                        color = if (i % 2 == 0) baseColor else Color.White,
                        maxRadius = (4..9).random().toFloat()
                    )
                )
            }

            val animDuration = (280 / speedMultiplier).toInt().coerceAtLeast(140)

            // Forward strike snap
            strikeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween((animDuration * 0.45).toInt(), easing = FastOutSlowInEasing)
            )

            // Retract to guard
            strikeProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween((animDuration * 0.55).toInt(), easing = FastOutSlowInEasing)
            )

            calloutAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(220)
            )

            particles.clear()
            onAnimationComplete()
        }
    }

    // Particle expansion frame loop
    LaunchedEffect(strikeProgress.value) {
        val p = strikeProgress.value
        particles.forEach {
            it.progress = p
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width * 0.5f
            val centerY = height * 0.52f

            // 1. Draw Ring Mat / Floor Perspective Grid
            drawFloorGrid(centerX, height)

            // 2. Idle rhythmic bounce offset
            val isStriking = strikeProgress.value > 0.01f
            val bounceY = if (!isStriking) sin(bouncePhase) * 12f else 0f
            val bounceX = if (!isStriking) cos(bouncePhase) * 4f else 0f

            // Dynamic stance mirroring (orthodox vs southpaw)
            val mirror = if (isSouthpaw) -1f else 1f

            // 3. Fighter Kinematics
            drawCombatFighter(
                centerX = centerX + bounceX,
                centerY = centerY + bounceY,
                canvasWidth = width,
                canvasHeight = height,
                technique = lastExecutedStrike,
                progress = strikeProgress.value,
                mirror = mirror,
                particles = particles
            )
        }

        // Animated Callout Label Overlay
        if (calloutAlpha.value > 0.05f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = activeCalloutText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = when (lastExecutedStrike) {
                        CombatTechnique.CROSS, CombatTechnique.REAR_UPPERCUT -> CrimsonRed
                        CombatTechnique.JAB -> NeonCyan
                        CombatTechnique.LEAD_HOOK, CombatTechnique.REAR_HOOK -> ElectricGold
                        CombatTechnique.MUAY_THAI_KNEE, CombatTechnique.REAR_LOW_KICK -> NeonLime
                        else -> ElectricGold
                    }.copy(alpha = calloutAlpha.value)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Canvas Drawing Engine for Fighter
// -------------------------------------------------------------
private fun DrawScope.drawFloorGrid(centerX: Float, height: Float) {
    val matY = height * 0.88f
    val horizonY = height * 0.72f

    // Floor glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(CrimsonRed.copy(alpha = 0.22f), Color.Transparent),
            center = Offset(centerX, matY - 20f),
            radius = size.width * 0.45f
        ),
        radius = size.width * 0.45f,
        center = Offset(centerX, matY - 20f)
    )

    // Shadow below fighter
    drawOval(
        color = Color(0xFF000000).copy(alpha = 0.55f),
        topLeft = Offset(centerX - 100f, matY - 14f),
        size = androidx.compose.ui.geometry.Size(200f, 28f)
    )

    // Perspective floor lines
    for (i in -3..3) {
        val startX = centerX + i * 40f
        val endX = centerX + i * 110f
        drawLine(
            color = CrimsonRed.copy(alpha = 0.18f),
            start = Offset(startX, horizonY),
            end = Offset(endX, height),
            strokeWidth = 2f
        )
    }

    // Outer ring ellipse
    drawArc(
        color = CrimsonRed.copy(alpha = 0.35f),
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(centerX - size.width * 0.38f, matY - 32f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.76f, 64f),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawCombatFighter(
    centerX: Float,
    centerY: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    technique: CombatTechnique?,
    progress: Float,
    mirror: Float,
    particles: List<SparkParticle>
) {
    // Fighter dimension scale
    val scale = (canvasHeight / 420f).coerceIn(0.75f, 1.4f)
    val headRadius = 24f * scale
    val torsoLength = 80f * scale
    val armLength = 52f * scale
    val legLength = 85f * scale

    // Lean and Dodge Calculations
    var torsoTiltX = 0f
    var torsoTiltY = 0f
    var leadPunchExtension = 0f
    var rearPunchExtension = 0f
    var hookArcProgress = 0f
    var uppercutRiseProgress = 0f
    var kneeRiseProgress = 0f
    var kickArcProgress = 0f
    var guardTightness = 0f

    when (technique) {
        CombatTechnique.JAB -> {
            leadPunchExtension = progress
            torsoTiltX = -12f * progress * mirror
        }
        CombatTechnique.CROSS -> {
            rearPunchExtension = progress
            torsoTiltX = 18f * progress * mirror
        }
        CombatTechnique.LEAD_HOOK -> {
            hookArcProgress = progress
            torsoTiltX = -16f * progress * mirror
        }
        CombatTechnique.REAR_HOOK -> {
            hookArcProgress = -progress
            torsoTiltX = 16f * progress * mirror
        }
        CombatTechnique.LEAD_UPPERCUT -> {
            uppercutRiseProgress = progress
            torsoTiltY = 12f * (1f - progress)
        }
        CombatTechnique.REAR_UPPERCUT -> {
            uppercutRiseProgress = progress
            torsoTiltX = 14f * progress * mirror
        }
        CombatTechnique.SLIP_LEFT -> {
            torsoTiltX = -48f * progress * mirror
            torsoTiltY = 14f * progress
        }
        CombatTechnique.SLIP_RIGHT -> {
            torsoTiltX = 48f * progress * mirror
            torsoTiltY = 14f * progress
        }
        CombatTechnique.ROLL_WEAVE -> {
            torsoTiltY = 46f * sin(progress * PI.toFloat())
            torsoTiltX = 32f * cos(progress * PI.toFloat()) * mirror
        }
        CombatTechnique.GUARD_BLOCK -> {
            guardTightness = progress
        }
        CombatTechnique.MUAY_THAI_KNEE -> {
            kneeRiseProgress = progress
            torsoTiltX = -10f * progress * mirror
            torsoTiltY = -12f * progress
        }
        CombatTechnique.LEAD_BODY_KICK, CombatTechnique.REAR_LOW_KICK -> {
            kickArcProgress = progress
            torsoTiltX = -22f * progress * mirror
        }
        null -> {}
    }

    // Kinematic Keypoints
    val hipX = centerX + torsoTiltX * 0.4f
    val hipY = centerY + 30f * scale + torsoTiltY * 0.5f

    val shoulderX = centerX + torsoTiltX
    val shoulderY = hipY - torsoLength + torsoTiltY

    val neckX = shoulderX
    val neckY = shoulderY - 14f * scale

    val headX = neckX + torsoTiltX * 0.3f
    val headY = neckY - headRadius

    // Legs (Stance Base)
    val leadFootX = hipX - 44f * scale * mirror
    val leadFootY = hipY + legLength
    val rearFootX = hipX + 46f * scale * mirror
    val rearFootY = hipY + legLength - 10f * scale

    // Leg joints
    val leadKneeX = (hipX + leadFootX) * 0.5f - 8f * scale * mirror
    val leadKneeY = (hipY + leadFootY) * 0.5f

    val rearKneeX = (hipX + rearFootX) * 0.5f + (if (kneeRiseProgress > 0f) -40f * kneeRiseProgress * mirror else 6f * scale * mirror)
    val rearKneeY = (hipY + rearFootY) * 0.5f - (if (kneeRiseProgress > 0f) 75f * kneeRiseProgress * scale else 0f)

    // Draw Legs
    val limbColor = Color(0xFF2C2F48)
    val limbAccent = CrimsonRed
    val strikeStroke = 12f * scale
    val bodyStroke = 16f * scale

    // Draw Rear Leg
    drawLine(
        color = limbColor,
        start = Offset(hipX, hipY),
        end = Offset(rearKneeX, rearKneeY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = if (kneeRiseProgress > 0.5f) NeonLime else limbColor,
        start = Offset(rearKneeX, rearKneeY),
        end = Offset(rearFootX, if (kneeRiseProgress > 0f) rearKneeY + 45f * scale else rearFootY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )

    // Draw Lead Leg
    val leadKickX = if (kickArcProgress > 0f) leadFootX - 90f * kickArcProgress * mirror else leadFootX
    val leadKickY = if (kickArcProgress > 0f) leadFootY - 60f * kickArcProgress * scale else leadFootY
    drawLine(
        color = limbColor,
        start = Offset(hipX, hipY),
        end = Offset(leadKneeX, leadKneeY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = if (kickArcProgress > 0.4f) NeonLime else limbColor,
        start = Offset(leadKneeX, leadKneeY),
        end = Offset(leadKickX, leadKickY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )

    // Torso / Chest / Core
    val chestPath = Path().apply {
        moveTo(shoulderX - 32f * scale, shoulderY)
        lineTo(shoulderX + 32f * scale, shoulderY)
        lineTo(hipX + 20f * scale, hipY)
        lineTo(hipX - 20f * scale, hipY)
        close()
    }
    drawPath(
        path = chestPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3B4060), Color(0xFF23253A)),
            startY = shoulderY,
            endY = hipY
        )
    )

    // Belt / Waistband
    drawLine(
        color = CrimsonRed,
        start = Offset(hipX - 21f * scale, hipY - 2f),
        end = Offset(hipX + 21f * scale, hipY - 2f),
        strokeWidth = 6f * scale,
        cap = StrokeCap.Round
    )

    // Head
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF4C527D), Color(0xFF1E2135)),
            center = Offset(headX, headY),
            radius = headRadius
        ),
        radius = headRadius,
        center = Offset(headX, headY)
    )

    // Boxer Headband
    drawLine(
        color = CrimsonRed,
        start = Offset(headX - headRadius * 0.95f, headY - 4f * scale),
        end = Offset(headX + headRadius * 0.95f, headY - 4f * scale),
        strokeWidth = 6f * scale,
        cap = StrokeCap.Round
    )

    // Dynamic Arms & Boxing Gloves
    val leadShoulderX = shoulderX - 28f * scale * mirror
    val leadShoulderY = shoulderY + 4f * scale
    val rearShoulderX = shoulderX + 28f * scale * mirror
    val rearShoulderY = shoulderY + 4f * scale

    // Guard Positions (Chin level)
    val defaultLeadGloveX = leadShoulderX - 16f * scale * mirror
    val defaultLeadGloveY = neckY - 6f * scale

    val defaultRearGloveX = rearShoulderX - 12f * scale * mirror
    val defaultRearGloveY = neckY - 4f * scale

    // Calculate actual glove positions based on strike
    var leadGloveX = defaultLeadGloveX
    var leadGloveY = defaultLeadGloveY
    var rearGloveX = defaultRearGloveX
    var rearGloveY = defaultRearGloveY

    var leadElbowX = (leadShoulderX + defaultLeadGloveX) * 0.5f - 14f * scale * mirror
    var leadElbowY = (leadShoulderY + defaultLeadGloveY) * 0.5f + 16f * scale

    var rearElbowX = (rearShoulderX + defaultRearGloveX) * 0.5f + 14f * scale * mirror
    var rearElbowY = (rearShoulderY + defaultRearGloveY) * 0.5f + 16f * scale

    // JAB (Lead Straight)
    if (leadPunchExtension > 0f) {
        val reach = 135f * scale * leadPunchExtension
        leadGloveX = leadShoulderX - reach * mirror
        leadGloveY = leadShoulderY - 4f * scale
        leadElbowX = (leadShoulderX + leadGloveX) * 0.5f
        leadElbowY = leadShoulderY
    }

    // CROSS (Rear Straight Power Punch)
    if (rearPunchExtension > 0f) {
        val reach = 150f * scale * rearPunchExtension
        rearGloveX = rearShoulderX - reach * mirror
        rearGloveY = rearShoulderY - 4f * scale
        rearElbowX = (rearShoulderX + rearGloveX) * 0.5f
        rearElbowY = rearShoulderY - 2f * scale
    }

    // HOOK (Lead Hook Arc)
    if (hookArcProgress != 0f) {
        val arc = hookArcProgress
        val angle = (arc * PI * 0.65f).toFloat()
        val radius = 105f * scale
        leadGloveX = leadShoulderX - (cos(angle) * radius) * mirror
        leadGloveY = leadShoulderY - sin(angle) * 35f * scale
        leadElbowX = leadShoulderX - 55f * scale * mirror
        leadElbowY = leadShoulderY - 10f * scale
    }

    // UPPERCUT (Rising Punch)
    if (uppercutRiseProgress > 0f) {
        val rise = uppercutRiseProgress
        leadGloveX = neckX - 10f * scale * mirror
        leadGloveY = neckY - 75f * rise * scale
        leadElbowX = leadShoulderX
        leadElbowY = shoulderY + 20f * scale
    }

    // GUARD BLOCK (Tight Shell)
    if (guardTightness > 0f) {
        leadGloveX = neckX - 8f * scale * mirror
        leadGloveY = headY - 4f * scale
        rearGloveX = neckX + 8f * scale * mirror
        rearGloveY = headY - 4f * scale
    }

    // Draw Rear Arm first (behind torso)
    drawLine(
        color = limbColor,
        start = Offset(rearShoulderX, rearShoulderY),
        end = Offset(rearElbowX, rearElbowY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = limbColor,
        start = Offset(rearElbowX, rearElbowY),
        end = Offset(rearGloveX, rearGloveY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )

    // Rear Boxing Glove
    drawCircle(
        color = CrimsonRed,
        radius = 16f * scale,
        center = Offset(rearGloveX, rearGloveY)
    )

    // Draw Lead Arm (front of torso)
    drawLine(
        color = Color(0xFF3F4468),
        start = Offset(leadShoulderX, leadShoulderY),
        end = Offset(leadElbowX, leadElbowY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF3F4468),
        start = Offset(leadElbowX, leadElbowY),
        end = Offset(leadGloveX, leadGloveY),
        strokeWidth = strikeStroke,
        cap = StrokeCap.Round
    )

    // Lead Boxing Glove
    val isLeadStriking = leadPunchExtension > 0.5f || hookArcProgress > 0.5f || uppercutRiseProgress > 0.5f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (isLeadStriking) NeonCyan else CrimsonRed,
                CrimsonDark
            ),
            center = Offset(leadGloveX, leadGloveY),
            radius = 18f * scale
        ),
        radius = 17f * scale,
        center = Offset(leadGloveX, leadGloveY)
    )

    // Motion Streak & Punch Impact Shockwaves
    val impactPoint: Offset? = when {
        leadPunchExtension > 0.7f -> Offset(leadGloveX, leadGloveY)
        rearPunchExtension > 0.7f -> Offset(rearGloveX, rearGloveY)
        kneeRiseProgress > 0.7f -> Offset(rearKneeX, rearKneeY)
        kickArcProgress > 0.7f -> Offset(leadKickX, leadKickY)
        hookArcProgress > 0.7f -> Offset(leadGloveX, leadGloveY)
        uppercutRiseProgress > 0.7f -> Offset(leadGloveX, leadGloveY)
        else -> null
    }

    if (impactPoint != null && progress > 0.5f) {
        // Impact ring explosion
        val rippleRadius = (progress - 0.5f) * 80f * scale
        val alpha = (1f - (progress - 0.5f) * 2f).coerceIn(0f, 1f)

        drawCircle(
            color = ElectricGold.copy(alpha = alpha),
            radius = rippleRadius,
            center = impactPoint,
            style = Stroke(width = 4f * scale)
        )

        drawCircle(
            color = CrimsonRed.copy(alpha = alpha * 0.8f),
            radius = rippleRadius * 0.6f,
            center = impactPoint,
            style = Stroke(width = 6f * scale)
        )

        // Draw Sparks radiating outward
        particles.forEach { p ->
            val curX = impactPoint.x + p.vx * p.progress * 0.05f
            val curY = impactPoint.y + p.vy * p.progress * 0.05f
            val sparkAlpha = (1f - p.progress).coerceIn(0f, 1f)
            drawCircle(
                color = p.color.copy(alpha = sparkAlpha),
                radius = p.maxRadius * (1f - p.progress * 0.5f),
                center = Offset(curX, curY)
            )
        }
    }

    // Guard Shield Graphic if blocking
    if (guardTightness > 0.2f) {
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.6f * guardTightness), Color.Transparent),
                center = Offset(neckX, headY),
                radius = 65f * scale
            ),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(neckX - 60f * scale, headY - 50f * scale),
            size = androidx.compose.ui.geometry.Size(120f * scale, 100f * scale),
            style = Stroke(width = 4f)
        )
    }
}
