package com.mitteloupe.loader.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> MultipleValueSelector(
    selectedOption: MutableState<T>,
    vararg options: Pair<String, T>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            Row {
                RadioButton(
                    selected = selectedOption.value == option.second,
                    onClick = { selectedOption.value = option.second }
                )
                Text(
                    text = option.first,
                    color = color,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}
