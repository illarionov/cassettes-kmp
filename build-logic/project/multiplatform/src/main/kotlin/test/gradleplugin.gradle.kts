/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.cassettes.gradle.multiplatform.test

/*
 * Convention plugin that configures unit and functional tests used to test Gradle plugins.
 *
 * Enables publishing the plugin to the testing repository
 */
plugins {
    `java-gradle-plugin`
    id("at.released.cassettes.gradle.multiplatform.publish.base")
}

private val functionalTestRepository = layout.buildDirectory.dir("functional-tests-plugin-repository")

publishing {
    repositories {
        maven {
            name = "functionalTests"
            setUrl(functionalTestRepository)
        }
    }
}

private val libs: VersionCatalog = versionCatalogs.named("libs")

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.findVersion("junit5").get().toString())
            targets {
                all {
                    testTask.configure {
                        configureTestTaskDefaults(this)
                    }
                }
            }
            dependencies {
                implementation(platform(libs.findLibrary("junit.bom").get()))

                implementation(libs.findLibrary("assertk").get())
                implementation(libs.findLibrary("junit.jupiter").get())
                implementation(libs.findLibrary("junit.jupiter.params").get())
                implementation(libs.findLibrary("mockk").get())
                runtimeOnly(libs.findLibrary("junit.platform.launcher").get())
            }
        }
        withType(JvmTestSuite::class).matching {
            it.name in setOf("functionalTest")
        }.configureEach {
            useJUnitJupiter(libs.findVersion("junit5").get().toString())

            dependencies {
                implementation(project())
                implementation(libs.findLibrary("assertk").get())
            }

            targets {
                all {
                    testTask.configure {
                        configureTestTaskDefaults(this)
                        dependsOn(tasks.named("publishAllPublicationsToFunctionalTestsRepository"))
                        environment["TEST_CASSETTES_VERSION"] = providers.environmentVariable("TEST_CASSETTES_VERSION")
                            .orElse(project.version.toString())
                            .get()
                        environment["TEST_KOTLIN_VERSION"] = providers.environmentVariable("TEST_KOTLIN_VERSION")
                            .orElse(libs.findVersion("kotlin").get().toString())
                            .get()
                        inputs.dir(functionalTestRepository)
                        shouldRunAfter(test)
                    }
                }
            }
        }
        register<JvmTestSuite>("functionalTest")
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    dependsOn(testing.suites.named("functionalTest"))
}
