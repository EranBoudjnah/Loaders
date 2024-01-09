# Loaders

A collection of progress indicators/loaders for Jetpack Compose

[![Gradle checks](https://github.com/EranBoudjnah/Loaders/actions/workflows/gradle-checks.yml/badge.svg)](https://github.com/EranBoudjnah/Loaders/actions/workflows/gradle-checks.yml)
![Gears - Maven Central](https://img.shields.io/maven-central/v/com.mitteloupe.loaders/loaders-gears?label=Gears%20on%20maven-central)

<img src="https://github.com/EranBoudjnah/Loaders/blob/ca0d8f703f6b809b02beeeae5696fc5232f54e3b/Assets/main_demo.gif" width="320" height="600" />

Download
--------
Update the project build.gradle:

```gradle
dependencies {
   implementation 'com.mitteloupe.loaders:loaders-gears:0.2.0'
}
```

### Usage

```kotlin
GearsLoader()
```

#### Other optional params:

```kotlin
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

### Show some ❤ and support

If this project helped you, give it a ⭐️!

### Contributing

Contributions to this project are welcome. Please feel free to report any issues or fork to
make changes and raise a pull request.

### Licence

This project is distributed under the terms of the MIT License. See [LICENSE.md](LICENSE) for
details.
