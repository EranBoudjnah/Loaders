plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        withSourcesJar()
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(
                    project.dependencies.platform(libs.compose.bom)
                )
                implementation(libs.androidx.compose.ui.ui2)
                implementation(libs.androidx.compose.ui.ui.graphics2)
                implementation(libs.androidx.compose.ui.ui.tooling.preview2)
                implementation(libs.androidx.compose.material3.material32)
                implementation(libs.kotlinx.datetime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockito.kotlin)
                implementation(libs.mockito.inline)
            }
        }
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.ui.tooling2)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest2)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit42)
}

android {
    namespace = "com.mitteloupe.loader.trigonometry"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

ext {
    set("PUBLISH_ARTIFACT_ID", "loaders-trigonometry")
    set("PUBLISH_VERSION", "0.1.0")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

apply {
    from("release-jar.gradle")
}
