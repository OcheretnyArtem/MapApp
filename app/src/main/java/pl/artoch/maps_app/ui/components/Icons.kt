package pl.artoch.maps_app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Icon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Icon(
        modifier = modifier.sizeIn(size),
        painter = painterResource(id = iconRes),
        contentDescription = ""
    )
}

