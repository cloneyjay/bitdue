package com.bitdue.financeapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo

/**
 * Bounce animation on tap
 */
fun Modifier.bounceClick(
    scale: Float = 0.95f
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "bounceClick"
        value = scale
    }
) {
    var isPressed by remember { mutableStateOf(false) }
    val scaleValue by animateFloatAsState(
        targetValue = if (isPressed) scale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )
    
    this
        .graphicsLayer {
            scaleX = scaleValue
            scaleY = scaleValue
        }
}

/**
 * Shimmer loading effect
 */
@Composable
fun shimmerBrush(
    widthPx: Float,
    showShimmer: Boolean = true,
    targetValue: Float = 1000f
): androidx.compose.ui.graphics.Brush {
    return if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )
        
        androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(
                androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.6f),
                androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.2f),
                androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.6f)
            ),
            start = androidx.compose.ui.geometry.Offset(translateAnimation.value - widthPx, 0f),
            end = androidx.compose.ui.geometry.Offset(translateAnimation.value, 0f)
        )
    } else {
        androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(
                androidx.compose.ui.graphics.Color.Transparent,
                androidx.compose.ui.graphics.Color.Transparent
            )
        )
    }
}
