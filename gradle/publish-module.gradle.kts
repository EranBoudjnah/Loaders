import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

apply(plugin = "maven-publish")
apply(plugin = "signing")

val gitUrl = "https://github.com/EranBoudjnah/Loaders.git"

configure<PublishingExtension> {
    repositories {
        mavenLocal()
        maven {
            name = "SonatypeStaging"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = (project.findProperty("ossrhUsername") ?: System.getenv("OSSRH_USERNAME"))?.toString()
                password = (project.findProperty("ossrhPassword") ?: System.getenv("OSSRH_PASSWORD"))?.toString()
            }
        }
    }

    publications.withType<MavenPublication> {
        artifactId = if (name == "kotlinMultiplatform" || !project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            "loaders-${project.name}"
        } else {
            "loaders-${project.name}-$name"
        }

        pom {
            name.set("loaders-${project.name}")
            description.set(project.description)
            url.set(gitUrl)
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/EranBoudjnah/Loaders/blob/master/LICENSE")
                }
            }
            scm {
                connection.set(gitUrl)
                developerConnection.set(gitUrl)
                url.set(gitUrl)
            }
            developers {
                developer {
                    id.set("EranBoudjnah")
                    name.set("Eran Boudjnah")
                    email.set("eranbou+loaders@gmail.com")
                }
            }
        }
    }
}

configure<SigningExtension> {
    gradle.taskGraph.whenReady {
        val isPublishToSonatype = allTasks.any { it.name.contains("publishMavenPublicationToSonatypeStagingRepository") }
        if (isPublishToSonatype) {
            sign(extensions.getByType<PublishingExtension>().publications)
        }
    }
}
