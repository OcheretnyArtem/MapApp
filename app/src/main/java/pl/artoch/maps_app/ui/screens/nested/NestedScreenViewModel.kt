package pl.artoch.maps_app.ui.screens.nested

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface NestedScreenContainer {
    interface ViewModel {
        val viewState: StateFlow<ViewState>
    }

    data class ViewState(val username: String)

    @AssistedFactory
    interface Factory {
        fun create(username: String): NestedScreenViewModel
    }
}

@HiltViewModel(assistedFactory = NestedScreenContainer.Factory::class)
class NestedScreenViewModel @AssistedInject constructor(
    @Assisted private val username: String
) : ViewModel(), NestedScreenContainer.ViewModel {
    override val viewState = MutableStateFlow(NestedScreenContainer.ViewState(username = username))
}
