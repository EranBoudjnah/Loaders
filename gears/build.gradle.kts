plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.mitteloupe.loader.gears"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withSourcesJar()
    }

    jvmToolchain(JavaVersion.VERSION_17.majorVersion.toInt())

    sourceSets {
        val androidMain by getting

        val commonMain by getting {
            dependencies {
                api(libs.loaders.trigonometry)
                implementation(project.dependencies.platform(libs.compose.bom))
                implementation(libs.androidx.ui)
                implementation(libs.androidx.ui.graphics)
                implementation(libs.ui.tooling.preview)
                implementation(libs.material3)
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

description = "Gears Loader."

ext {
    set("PUBLISH_ARTIFACT_ID", "loaders-gears")
    set("PUBLISH_VERSION", libs.versions.loadersGears.get())
}

apply(from = "release-jar.gradle")
