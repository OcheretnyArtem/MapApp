package pl.artoch.maps_app.ui.screens.stories

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import pl.artoch.maps_app.domain.Story
import pl.artoch.maps_app.ui.components.stories.Stories

@Composable
fun StoriesScreen(viewModel: StoriesContract.ViewModel, innerPadding: PaddingValues, navController: NavController) {
    val viewState = viewModel.viewState.collectAsState()
    StoriesScreenUi(
        viewState = viewState.value,
        innerPadding = innerPadding,
        onStoriesEnds = navController::popBackStack
    )
}

@Composable
private fun StoriesScreenUi(
    viewState: StoriesContract.ViewState,
    innerPadding: PaddingValues,
    onStoriesEnds: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { viewState.stories.size })
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Stories(
            numberOfPages = viewState.stories.size,
            indicatorModifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            pagerState = pagerState,
            onComplete = onStoriesEnds
        ) { index ->
            Story(story = viewState.stories[index])
        }
    }
}

@Composable
private fun Story(story: Story) {
    when (story.type) {
        Story.Type.VIDEO -> VideoStory(story.url)
        Story.Type.IMAGE -> ImageStory(story.url)
    }
}

@Composable
private fun ImageStory(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize(),
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoStory(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
            play()
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(context).apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                player = exoPlayer
                useController = false
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    )
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Preview
@Composable
private fun StoriesScreenPreview() {
    StoriesScreenUi(
        viewState = StoriesContract.ViewState(stories = listOf()),
        innerPadding = PaddingValues(),
        onStoriesEnds = {}
    )
}
