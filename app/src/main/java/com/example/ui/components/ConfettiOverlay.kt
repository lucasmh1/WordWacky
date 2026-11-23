package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.BerryAccent
import com.example.ui.theme.CobaltSecondary
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.MarigoldTertiary
import com.example.ui.theme.MintAccent
import com.example.ui.theme.PlumAccent
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val startXFraction: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val sizeDp: Float,
    val isCircle: Boolean,
    val color: Color,
    val wobblePhase: Float
)

@Composable
fun ConfettiBurstOverlay(
    triggerKey: Int,
    modifier: Modifier = Modifier
) {
    if (triggerKey <= 0) return

    val palette = remember {
        listOf(CoralPrimary, CobaltSecondary, MarigoldTertiary, MintAccent, PlumAccent, BerryAccent)
    }

    val pieces = remember(triggerKey) {
        val rng = Random(triggerKey * 9973)
        List(58) {
            ConfettiPiece(
                startXFraction = rng.nextFloat(),
                velocityX = (rng.nextFloat() - 0.5f) * 260f,
                velocityY = 260f + rng.nextFloat() * 480f,
                rotationSpeed = (rng.nextFloat() - 0.5f) * 540f,
                sizeDp = 7f + rng.nextFloat() * 7f,
                isCircle = rng.nextBoolean(),
                color = palette[rng.nextInt(palette.size)],
                wobblePhase = rng.nextFloat() * 6.28f
            )
        }
    }

    val progress = remember(triggerKey) { Animatable(0f) }

    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    val t = progress.value
    if (t >= 1f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val alpha = (1f - ((t - 0.65f).coerceAtLeast(0f) / 0.35f)).coerceIn(0f, 1f)
        val gravity = 680f
        pieces.forEach { piece ->
            val x = size.width * piece.startXFraction +
                piece.velocityX * t +
                sin((t * 8f + piece.wobblePhase).toDouble()).toFloat() * 28f
            val y = -40f + piece.velocityY * t + 0.5f * gravity * t * t
            if (y <= size.height + 60f) {
                val pxSize = piece.sizeDp * density
                val color = piece.color.copy(alpha = alpha)
                if (piece.isCircle) {
                    drawCircle(
                        color = color,
                        radius = pxSize * 0.45f,
                        center = Offset(x, y)
                    )
                } else {
                    rotate(
                        degrees = piece.rotationSpeed * t + cos(piece.wobblePhase.toDouble()).toFloat() * 45f,
                        pivot = Offset(x, y)
                    ) {
                        drawRect(
                            color = color,
                            topLeft = Offset(x - pxSize / 2f, y - pxSize * 0.3f),
                            size = Size(pxSize, pxSize * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
