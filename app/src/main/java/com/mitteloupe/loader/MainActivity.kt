package com.mitteloupe.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.GearsLoader
import com.mitteloupe.loader.ui.theme.LoadersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GearsLoader(
                        overflow = true,
                        holeRadius = 4f.dp,
                        toothDepth = 6f.dp,
                        toothWidth = 6f.dp,
                        toothRoundness = 3f.dp,
                        modifier = Modifier
                            .width(350.dp)
                            .height(200.dp)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GearsLoader(
                overflow = false,
                holeRadius = 4f.dp,
                toothDepth = 6f.dp,
                toothWidth = 6f.dp,
                toothRoundness = 3f.dp,
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            )
        }
    }
}
