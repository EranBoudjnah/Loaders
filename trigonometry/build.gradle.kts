plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.mitteloupe.loader.trigonometry"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withSourcesJar()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.compose.bom))
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
    "androidRuntimeClasspath"(libs.ui.tooling)
    "androidRuntimeClasspath"(libs.ui.test.manifest)
}

description = "Loaders Trigonometry."

ext {
    set("PUBLISH_ARTIFACT_ID", "loaders-trigonometry")
    set("PUBLISH_VERSION", libs.versions.loadersTrigonometry.get())
}

apply(from = "release-jar.gradle")
