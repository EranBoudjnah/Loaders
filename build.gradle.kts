import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.android.library") version "8.2.1" apply false
    kotlin("multiplatform") version "1.9.21" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
}

configure<KtlintExtension> {
    android.set(true)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType(Test::class) {
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
