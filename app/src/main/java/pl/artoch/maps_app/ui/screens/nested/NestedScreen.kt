package pl.artoch.maps_app.ui.screens.nested

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NestedScreen(viewModel: NestedScreenContainer.ViewModel) {
    val viewState = viewModel.viewState.collectAsState()
    NestedScreen(viewState.value)
}

@Composable
private fun NestedScreen(viewState: NestedScreenContainer.ViewState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(viewState.username)
    }
}

@Preview
@Composable
private fun NestedScreenPreview() {
    NestedScreen(NestedScreenContainer.ViewState("Preview"))
}
