# Loaders

A collection of progress indicators/loaders for Jetpack Compose

[![Gradle checks](https://github.com/EranBoudjnah/Loaders/actions/workflows/gradle-checks.yml/badge.svg)](https://github.com/EranBoudjnah/Loaders/actions/workflows/gradle-checks.yml)
![Gears - Maven Central](https://img.shields.io/maven-central/v/com.mitteloupe.loaders/loaders-gears?label=Gears%20on%20maven-central)
![Jigsaw - Maven Central](https://img.shields.io/maven-central/v/com.mitteloupe.loaders/loaders-jigsaw?label=Jigsaw%20on%20maven-central)

![Gears](https://github.com/EranBoudjnah/Loaders/blob/ca0d8f703f6b809b02beeeae5696fc5232f54e3b/Assets/main_demo.gif)
![Jigsaw](https://github.com/EranBoudjnah/Loaders/blob/fd4493f962408b9ec05c6e5c2f1893c0210d81b3/Assets/jigsaw_demo.gif)

Download
--------
Update the project build.gradle. You can include one or more of the loaders:

```gradle
dependencies {
   implementation 'com.mitteloupe.loaders:loaders-gears:0.4.0'
   implementation 'com.mitteloupe.loaders:loaders-jigsaw:0.2.0'
}
```

This project is hosted in mavenCentral().

### Usage

```kotlin
GearsLoader()
JigsawLoader()
```

#### Other optional params:

##### Gears

```kotlin
@Composable
fun GearsLoader(
    modifier: Modifier = Modifier,
    gearConfiguration: GearConfiguration = GearsLoaderDefaults.gearConfiguration,
    @FloatRange(from = 0.0, to = 1.0) toothRoundness: Float = GearsLoaderDefaults.TOOTH_ROUNDNESS,
    holeRadius: Dp = GearsLoaderDefaults.holeRadius,
    rotationTimeMilliseconds: Int = GearsLoaderDefaults.ROTATION_TIME_MILLISECONDS,
    color: Color = GearsLoaderDefaults.color,
    trackColor: Color = GearsLoaderDefaults.trackColor,
    gearType: GearType = GearsLoaderDefaults.gearType,
    progressState: ProgressState = ProgressState.Indeterminate,
    transitionTimeMilliseconds: Int = DefaultDurationMillis,
    rectangleFiller: RectangleFiller = RectangleFiller(GearMesher())
)
```

##### Jigsaw

```kotlin
@Composable
fun JigsawLoader(
    modifier: Modifier = Modifier,
    progressState: ProgressState = Indeterminate(),
    horizontalPieces: Int = JigsawLoaderDefaults.horizontalPieces,
    verticalPieces: Int = JigsawLoaderDefaults.verticalPieces,
    puzzleBrushProvider: BrushProvider =
        ColorBrushProvider(JigsawLoaderDefaults.color),
    lightBrush: Brush = SolidColor(Color.White.copy(alpha = .4f)),
    darkBrush: Brush = SolidColor(Color.Black.copy(alpha = .6f)),
    trackColor: Color = JigsawLoaderDefaults.trackColor,
    piecePresenceResolver: PiecePresenceResolver = JigsawLoaderDefaults.piecePresenceResolver(
        progressState = progressState,
        horizontalPieces = horizontalPieces,
        verticalPieces = verticalPieces
    ),
    transitionTimeMilliseconds: Int = AnimationConstants.DefaultDurationMillis,
    @FloatRange(from = 0.0, to = 1.0) trackSaturation: Float = 1f,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean =
        JigsawLoaderDefaults.knobInversionEvaluator,
    knobConfiguration: KnobConfiguration = JigsawLoaderDefaults.knobConfiguration,
    overflow: Boolean = false
)
```

### Show some ❤ and support

If this project helped you, give it a ⭐️!

### Contributing

Contributions to this project are welcome. Please feel free to report any issues or fork to
make changes and raise a pull request.

### License

This project is distributed under the terms of the MIT License. See [LICENSE](LICENSE) for
details.
