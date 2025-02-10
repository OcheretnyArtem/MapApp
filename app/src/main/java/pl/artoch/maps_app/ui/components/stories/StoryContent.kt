package pl.artoch.maps_app.ui.components.stories

import android.view.MotionEvent
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StoryContent(
    pagerState: PagerState,
    onTap: (Boolean) -> Unit,
    content: @Composable (Int) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.pointerInteropFilter { motionEvent -> motionEvent.onMotionEvent(onTap) },
        beyondViewportPageCount = 5
    ) {
        content(it)
    }
}

private fun MotionEvent.onMotionEvent(onTap: (Boolean) -> Unit): Boolean {
    when (this.action) {
        MotionEvent.ACTION_DOWN -> {
            onTap(true)
        }
        MotionEvent.ACTION_UP -> {
            onTap(false)
        }
    }
    return true
}