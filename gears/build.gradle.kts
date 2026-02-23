plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

group = "com.mitteloupe.loaders"
version = libs.versions.loadersGears.get()

kotlin {
    androidLibrary {
        namespace = "com.mitteloupe.loader.gears"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withSourcesJar()
    }

    jvmToolchain(JavaVersion.VERSION_17.majorVersion.toInt())

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/java")
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
            kotlin.srcDir("src/test/java")
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

apply(from = "../gradle/publish-module.gradle.kts")
