/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/*
 * Convention plugin that configures Kotlin in projects with the Kotlin Multiplatform plugin
 */
plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    explicitApi = ExplicitApiMode.Warning

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }

    sourceSets {
        all {
            languageSettings {
                languageVersion = "1.9"
                apiVersion = "1.9"
                listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.ExperimentalStdlibApi",
                    "kotlinx.cinterop.ExperimentalForeignApi",
                ).forEach(::optIn)
            }
        }
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        freeCompilerArgs.addAll(
            "-Xjdk-release=11",
            "-Xjvm-default=all",
            "-Xlambdas=indy",
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}
