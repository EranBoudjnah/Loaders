package com.mitteloupe.loader.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SliderWithTitle(
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
        Slider(
            value = value.coerceIn(0f, 1f),
            onValueChange = onValueChange
        )
    }
}

@Preview
@Composable
fun Preview() {
    SliderWithTitle(text = "Volume", value = 0.5f, onValueChange = {})
}
