plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        withSourcesJar()
        publishLibraryVariants = listOf("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(
                    project.dependencies.platform(libs.compose.bom)
                )
                implementation(libs.androidx.compose.ui.ui)
                implementation(libs.androidx.compose.ui.ui.graphics)
                implementation(libs.androidx.compose.ui.ui.tooling.preview)
                implementation(libs.androidx.compose.material3.material3)
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
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}

android {
    namespace = "com.mitteloupe.loader.trigonometry"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

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

    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

ext {
    set("PUBLISH_ARTIFACT_ID", "loaders-trigonometry")
    set("PUBLISH_VERSION", "0.1.0")
}

val loadersSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

apply(from = "release-jar.gradle")
