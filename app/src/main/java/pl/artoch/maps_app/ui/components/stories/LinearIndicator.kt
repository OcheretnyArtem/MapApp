package pl.artoch.maps_app.ui.components.stories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults.ProgressAnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun LinearIndicator(
    modifier: Modifier = Modifier,
    startProgress: Boolean = false,
    trackColor: Color,
    progressColor: Color,
    slideDurationInSeconds: Long,
    isPaused: Boolean = false,
    onAnimationEnd: () -> Unit
) {
    val delayInMillis = rememberSaveable { (slideDurationInSeconds * 1000) / 100 }
    var progress by remember { mutableFloatStateOf(0.0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = ProgressAnimationSpec)
    if (startProgress) {
        LaunchedEffect(key1 = isPaused) {
            while (progress < 1f && isActive && isPaused.not()) {
                progress += 0.01f
                delay(delayInMillis)
            }
            if (isPaused.not()) {
                delay(200)
                onAnimationEnd()
            }
        }
    }

    RoundRectProgressIndicator(
        progress = animatedProgress,
        modifier = modifier,
        progressColor = progressColor,
        trackColor = trackColor,
        strokeWidth = 4.dp
    )
}

@Composable
private fun RoundRectProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color,
    trackColor: Color,
    strokeWidth: Dp,
) {
    val stroke: Float = LocalDensity.current.run { strokeWidth.toPx() }
    Canvas(
        modifier
            .progressSemantics(progress)
            .fillMaxWidth()
            .height(strokeWidth)
            .focusable()
    ) {
        drawRoundRectSegment(1f, trackColor, stroke)
        drawRoundRectSegment(progress, progressColor, stroke)
    }
}

private fun DrawScope.drawRoundRectSegment(
    progress: Float,
    color: Color,
    strokeWidth: Float,
) {
    val width = size.width
    val end = width * progress + progress
    val barEnd = (width).coerceAtMost(end)
    drawRoundRect(
        color = color,
        topLeft = Offset(0f, 0f),
        size = Size(barEnd, strokeWidth),
        cornerRadius = CornerRadius(strokeWidth / 2, strokeWidth / 2)
    )
}
