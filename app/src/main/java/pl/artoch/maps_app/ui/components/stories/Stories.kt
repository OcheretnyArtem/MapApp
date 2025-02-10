package pl.artoch.maps_app.ui.components.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Stories(
    numberOfPages: Int,
    indicatorModifier: Modifier = Modifier,
    pagerState: PagerState,
    spaceBetweenIndicator: Dp = 4.dp,
    indicatorTrackColor: Color = Color.LightGray,
    indicatorProgressColor: Color = Color.White,
    slideDurationInSeconds: Long = 5,
    touchToPause: Boolean = true,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit = {},
    content: @Composable (Int) -> Unit,
) {
    var isPaused by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        StoryContent(
            pagerState = pagerState,
            onTap = { if (touchToPause) isPaused = it },
            content = content
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenIndicator)
        ) {
            Spacer(modifier = Modifier.width(spaceBetweenIndicator))
            ListOfIndicators(
                numberOfPages = numberOfPages,
                modifier = indicatorModifier,
                trackColor = indicatorTrackColor,
                progressColor = indicatorProgressColor,
                slideDurationInSeconds = slideDurationInSeconds,
                isPaused = isPaused,
                pagerState = pagerState,
                onEveryStoryChange = onEveryStoryChange,
                onComplete = onComplete,
            )
            Spacer(modifier = Modifier.width(spaceBetweenIndicator))
        }
    }
}

@Composable
private fun RowScope.ListOfIndicators(
    numberOfPages: Int,
    modifier: Modifier,
    trackColor: Color,
    progressColor: Color,
    slideDurationInSeconds: Long,
    isPaused: Boolean,
    pagerState: PagerState,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
) {
    var currentPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()
    for (index in 0 until numberOfPages) {
        LinearIndicator(
            modifier = modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp)),
            startProgress = index == currentPage,
            trackColor = trackColor,
            progressColor = progressColor,
            slideDurationInSeconds = slideDurationInSeconds,
            isPaused = isPaused
        ) {
            coroutineScope.launch {
                currentPage++

                if (currentPage < numberOfPages) {
                    onEveryStoryChange?.invoke(currentPage)
                    pagerState.animateScrollToPage(currentPage)
                }

                if (currentPage == numberOfPages) {
                    onComplete()
                }
            }
        }
    }
}