/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("at.released.cassettes.gradle.multiplatform.publish.base")
    id("at.released.cassettes.gradle.multiplatform.test.jvm")
}

group = "at.released.cassettes"
version = cassettesVersions.getSubmoduleVersionProvider(
    propertiesFileKey = "cassettes_plugin_version",
    envVariableName = "CASSETTES_PLUGIN_VERSION",
).get()

gradlePlugin {
    website = "https://github.com/illarionov/cassettes-kmp"
    vcsUrl = "https://github.com/illarionov/cassettes-kmp"
}

mavenPublishing {
    extensions.getByType(SigningExtension::class.java).isRequired = false

    configure(GradlePlugin(javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml")))

    pom {
        description.set("Assets for Kotlin Multiplatform projects")
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Warning
    compilerOptions {
        jvmTarget.set(JVM_11)
        // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_6
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_6
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.assertk)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
}
