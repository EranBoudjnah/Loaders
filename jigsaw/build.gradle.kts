plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.mitteloupe.loader.jigsaw"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withSourcesJar()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.loaders.trigonometry)
                implementation(project.dependencies.platform(libs.compose.bom))
                implementation(libs.ui)
                implementation(libs.ui.graphics)
                implementation(libs.androidx.ui.tooling.preview)
                implementation(libs.androidx.material3)
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
    "androidRuntimeClasspath"(libs.ui.tooling)
    "androidRuntimeClasspath"(libs.ui.test.manifest)
}

description = "Jigsaw Loader."

ext {
    set("PUBLISH_ARTIFACT_ID", "loaders-jigsaw")
    set("PUBLISH_VERSION", libs.versions.loadersJigsaw.get())
}

apply(from = "release-jar.gradle")
