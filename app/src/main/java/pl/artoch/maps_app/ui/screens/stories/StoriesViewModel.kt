package pl.artoch.maps_app.ui.screens.stories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.artoch.maps_app.domain.Story
import javax.inject.Inject

private val stubStories = listOf(
    Story("https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg", Story.Type.IMAGE),
    Story("https://images.pexels.com/photos/1212487/pexels-photo-1212487.jpeg", Story.Type.IMAGE),
    Story("https://videos.pexels.com/video-files/4678261/4678261-hd_1080_1920_25fps.mp4", Story.Type.VIDEO),
    Story("https://images.pexels.com/photos/799443/pexels-photo-799443.jpeg", Story.Type.IMAGE),
)

interface StoriesContract {
    interface ViewModel {
        val viewState: StateFlow<ViewState>
    }

    data class ViewState(val stories: List<Story>)
}

@HiltViewModel
class StoriesViewModel @Inject constructor() : ViewModel(), StoriesContract.ViewModel {
    override val viewState = MutableStateFlow(StoriesContract.ViewState(stories = stubStories))
}
