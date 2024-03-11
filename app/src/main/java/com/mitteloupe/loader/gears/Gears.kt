package com.mitteloupe.loader.gears

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mitteloupe.loader.gears.model.GearConfiguration
import com.mitteloupe.loader.gears.model.ProgressState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gears(navController: NavHostController) {
    val progress = remember { mutableFloatStateOf(.75f) }
    val defaultColor = GearsLoaderDefaults.color
    val color = remember { mutableStateOf(defaultColor) }
    val minimumRadius = remember {
        mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.minimumRadius.value)
    }
    val maximumRadius = remember {
        mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.maximumRadius.value)
    }
    val gearType = remember { mutableStateOf(GearsLoaderDefaults.gearType) }
    val toothDepth = remember {
        mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.toothDepth.value)
    }
    val toothWidth = remember {
        mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.toothWidth.value)
    }
    val holeRadius =
        remember { mutableFloatStateOf(GearsLoaderDefaults.holeRadius.value) }
    val toothRoundness =
        remember { mutableFloatStateOf(GearsLoaderDefaults.TOOTH_ROUNDNESS) }
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text("Gears")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                GearsLoader(
                    gearConfiguration = GearConfiguration(
                        overflow = false,
                        minimumRadius = minimumRadius.floatValue.dp,
                        maximumRadius = maximumRadius.floatValue.dp,
                        toothDepth = toothDepth.floatValue.dp,
                        toothWidth = toothWidth.floatValue.dp
                    ),
                    toothRoundness = toothRoundness.floatValue,
                    holeRadius = holeRadius.floatValue.dp,
                    color = color.value,
                    gearType = gearType.value,
                    progressState = ProgressState.Determinate(
                        progress.floatValue
                    ),
                    modifier = Modifier
                        .width(352.dp)
                        .height(200.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                ControlPanel(
                    minimumRadius = minimumRadius,
                    maximumRadius = maximumRadius,
                    progress = progress,
                    color = color,
                    gearType = gearType,
                    toothDepth = toothDepth,
                    toothWidth = toothWidth,
                    holeRadius = holeRadius,
                    toothRoundness = toothRoundness
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Gears(rememberNavController())
}
