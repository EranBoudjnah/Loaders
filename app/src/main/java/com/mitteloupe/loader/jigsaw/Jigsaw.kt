package com.mitteloupe.loader.jigsaw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mitteloupe.loader.R
import com.mitteloupe.loader.jigsaw.BrushProvider.ImageResourceBrushProvider
import com.mitteloupe.loader.jigsaw.model.ProgressState
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Jigsaw(navController: NavHostController) {
    val progress = remember { mutableFloatStateOf(.75f) }
    val progressMode: MutableState<KClass<out ProgressState>> =
        remember { mutableStateOf(ProgressState.DeterminateSweep::class) }
    val knobConfiguration = remember { mutableStateOf(JigsawLoaderDefaults.knobConfiguration) }
    val horizontalPieces = remember { mutableIntStateOf(14) }
    val verticalPieces = remember { mutableIntStateOf(8) }
    val brushProvider: MutableState<BrushProvider> =
        remember { mutableStateOf(ImageResourceBrushProvider(R.drawable.jigsaw_image)) }

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
                    Text("Jigsaw")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val progressState by remember(progress.floatValue, progressMode.value) {
                    derivedStateOf {
                        when (progressMode.value) {
                            ProgressState.DeterminateSweep::class -> {
                                ProgressState.DeterminateSweep(progress.floatValue)
                            }

                            ProgressState.DeterminateSpiral::class -> {
                                ProgressState.DeterminateSpiral(progress.floatValue)
                            }

                            else -> ProgressState.Indeterminate()
                        }
                    }
                }
                JigsawLoader(
                    progressState = progressState,
                    puzzleBrushProvider = brushProvider.value,
                    horizontalPieces = horizontalPieces.intValue,
                    verticalPieces = verticalPieces.intValue,
                    knobConfiguration = knobConfiguration.value,
                    modifier = Modifier
                        .width(352.dp)
                        .height(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
                ControlPanel(
                    progress = progress,
                    progressMode = progressMode,
                    knobConfiguration = knobConfiguration,
                    horizontalPieces = horizontalPieces,
                    verticalPieces = verticalPieces,
                    brushProvider = brushProvider,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Jigsaw(rememberNavController())
}
