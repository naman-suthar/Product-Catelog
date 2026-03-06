package app.naman.lumostest.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    // ── Capture theme tokens (Canvas lambdas are not composable) ───────────────
    val primary          = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimary        = MaterialTheme.colorScheme.onPrimary
    val onSurface        = MaterialTheme.colorScheme.onSurface
    val background       = MaterialTheme.colorScheme.background

    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        started = true
        delay(2600L)
        onSplashComplete()
    }

    // ── Logo entrance ──────────────────────────────────────────────────────────
    val logoScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "logo-scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(500),
        label = "logo-alpha"
    )

    // ── Text slide-up ──────────────────────────────────────────────────────────
    val textAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(700, delayMillis = 450),
        label = "text-alpha"
    )
    val textOffsetDp by animateDpAsState(
        targetValue = if (started) 0.dp else 32.dp,
        animationSpec = tween(700, delayMillis = 450, easing = FastOutSlowInEasing),
        label = "text-offset"
    )

    // ── Outer glow pulse ───────────────────────────────────────────────────────
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f, targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "pulse-scale"
    )
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.30f, targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )

    // ── Layout ─────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryContainer.copy(alpha = 0.20f),
                        background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // ── Logo ──────────────────────────────────────────────────────────
            Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {

                // Outer pulse ring
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = if (started) pulseAlpha else 0f
                        }
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primary, Color.Transparent),
                            center = center,
                            radius = size.minDimension / 2f
                        ),
                        radius = size.minDimension / 2f
                    )
                }

                // Badge circle + rays
                Canvas(
                    modifier = Modifier
                        .size(140.dp)
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            alpha = logoAlpha
                        }
                ) {
                    val c = center
                    val r = size.minDimension / 2f

                    // Main fill: primaryContainer (center) → primary (edge)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryContainer, primary),
                            center = c,
                            radius = r
                        ),
                        radius = r,
                        center = c
                    )

                    // Off-centre specular highlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
                            center = Offset(c.x - r * 0.28f, c.y - r * 0.28f),
                            radius = r * 0.65f
                        ),
                        radius = r,
                        center = c
                    )

                    // Border ring
                    drawCircle(
                        color = primaryContainer.copy(alpha = 0.55f),
                        radius = r - 2f,
                        center = c,
                        style = Stroke(width = 2.5f)
                    )

                }

                // "LL" initials — sits on top of the primary-coloured circle
                Text(
                    text = "LL",
                    color = onPrimary,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── App name ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .offset(y = textOffsetDp)
                    .alpha(textAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LUMOS",
                    color = primary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 12.sp
                )
                Text(
                    text = "LOGIC",
                    color = onSurface.copy(alpha = 0.55f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 15.sp
                )

                Spacer(Modifier.height(22.dp))

                // Gradient divider
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, primary, Color.Transparent)
                            )
                        )
                )

            }
        }
    }
}
