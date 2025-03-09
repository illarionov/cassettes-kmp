/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform.publish

import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.credentials.AwsCredentials
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.withType

/*
 * Convention plugin with publishing defaults
 */
plugins {
    id("at.released.cassettes.gradle.documentation.dokka.subproject")
    id("at.released.cassettes.gradle.multiplatform.distribution.subproject")
    id("com.vanniktech.maven.publish.base")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

createCassettesVersionsExtension()

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    publishing {
        repositories {
            maven {
                name = "PixnewsS3"
                setUrl("s3://maven.pixnews.ru/")
                credentials(AwsCredentials::class) {
                    accessKey = providers.environmentVariable("YANDEX_S3_ACCESS_KEY_ID").getOrElse("")
                    secretKey = providers.environmentVariable("YANDEX_S3_SECRET_ACCESS_KEY").getOrElse("")
                }
            }
        }
    }

    signAllPublications()

    pom {
        name.set(project.name)
        description.set(
            "Assets for Kotlin Multiplatform projects",
        )
        url.set("https://github.com/illarionov/cassettes-kmp")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("illarionov")
                name.set("Alexey Illarionov")
                email.set("alexey@0xdc.ru")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/illarionov/cassettes-kmp.git")
            developerConnection.set("scm:git:ssh://github.com:illarionov/cassettes-kmp.git")
            url.set("https://github.com/illarionov/cassettes-kmp")
        }
    }
}
