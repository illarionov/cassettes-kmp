/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    `java-gradle-plugin`
    `kotlin-dsl-base`
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("at.released.cassettes.gradle.multiplatform.publish.base")
    id("at.released.cassettes.gradle.multiplatform.test.gradleplugin")
}

group = "at.released.cassettes"
version = cassettesVersions.getSubmoduleVersionProvider(
    propertiesFileKey = "cassettes_plugin_version",
    envVariableName = "CASSETTES_PLUGIN_VERSION",
).get()

private val internalCassettesApiMarker = "at.released.cassettes.plugin.InternalCassettesPluginApi"

gradlePlugin {
    website = "https://github.com/illarionov/cassettes-kmp"
    vcsUrl = "https://github.com/illarionov/cassettes-kmp"
    plugins.create("cassettesBase") {
        id = "at.released.cassettes.plugin.base"
        implementationClass = "at.released.cassettes.plugin.CassettesBasePlugin"
        displayName = "Base Cassettes Gradle Plugin"
    }
    plugins.create("unwrap") {
        id = "at.released.cassettes.plugin.unwrap"
        implementationClass = "at.released.cassettes.plugin.UnwrapPlugin"
        displayName = "Plugin that activates kotlin multiplatform resources and unpack assets for use"
    }
    plugins.create("rewrap") {
        id = "at.released.cassettes.plugin.rewrap"
        implementationClass = "at.released.cassettes.plugin.publish.RewrapPlugin"
        displayName = "A plugin that converts Cassette-based assets into native assets for the target platform"
        tags = listOf("kotlin", "multiplatform", "resources")
    }
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
        optIn = listOf(internalCassettesApiMarker)
    }
}

apiValidation {
    nonPublicMarkers.add(internalCassettesApiMarker)
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.agp.plugin.api)
    compileOnly(libs.kotlin.gradle.plugin)
    testImplementation(libs.agp.plugin.api)
    testImplementation(libs.assertk)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.gradle.plugin)
    testImplementation(libs.mockk)
    testImplementation(platform(libs.junit.bom))
    testRuntimeOnly(libs.junit.platform.launcher)
}
