package com.mitteloupe.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mitteloupe.loader.gears.Gears
import com.mitteloupe.loader.ui.theme.LoadersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                Gears()
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
        ) {
            Gears()
        }
    }
}
