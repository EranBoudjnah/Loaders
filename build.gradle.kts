import java.util.*
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.gradleNexus.publish)
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

val properties = Properties()
val propertiesFile = project.rootProject.file("local.properties")
if (propertiesFile.exists()) {
    properties.load(propertiesFile.inputStream())
}
val ossrhUsername = properties["ossrhUsername"]
val ossrhPassword = properties["ossrhPassword"]

extra.set("PUBLISH_GROUP_ID", "com.mitteloupe.loaders")

nexusPublishing {
    repositories {
        sonatype {
            username.set("$ossrhUsername")
            password.set("$ossrhPassword")
            packageGroup.set("${project.extra["PUBLISH_GROUP_ID"]}")
        }
    }
}
