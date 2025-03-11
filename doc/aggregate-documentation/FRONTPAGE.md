# Cassettes KMP

Assets for Kotlin Multiplatform projects.

A stub for developing a framework for bundling files with Kotlin Multiplatform projects.

Initial, experimental version: most features are currently non-functional. Will be heavily changed if development continues.

## Features

* Supports binary assets only — no special handling for resources, text, images, or videos.
* Does not depend on Compose.
* File reading is synchronous.
* Lets you publish assets in common source set for use on every target platform.
* Many features and improvements are planned.

Examples of binary files where a framework like this could be useful:

* Binary cross-platform files, like WebAssembly modules.
* ICU internationalization database.
* *libphonenumber* phone codes database.
* Pre-filled SQLite databases, for use with libraries like Room or SQLDelight.
* Cross-platform vector graphics and Compose animations.
* Audio and video files.
* Protobuf schemas.
* OpenGL textures.
* Neural network pretrained models used for inference.

## Publication format

The framework consists of Gradle plugins for asset packaging and a lightweight runtime library for asset reading.

Assets binary files are published to a Maven-compatible repository with Gradle Module Metadata for use
in Kotlin Multiplatform projects.

For Android target, files are packed as Android assets within AAR archives, so no additional Gradle plugins
are needed for their use.

For JVM target, files are packed as Java Resources, which can be handled with standard tools.

For native Kotlin Multiplatform targets (*iosArm64*, *iosSimulatorArm64*, *linuxX64*, *macosX64*, etc.), binaries
binaries are packaged and published in a format compatible with Kotlin Multiplatform Resources
To use it in your project, you can try the [Compose Multiplatform Resources] plugin, or
*at.released.cassettes.plugin.unwrap* Gradle plugin.

To simplify loading binaries across different platforms, a helper library *cassettes-playhead* is available.

## Related Libraries

Here are some other Kotlin Multiplatform resource-handling libraries you might find useful:

* [Compose Multiplatform Resources]
* [Moko Resources](https://github.com/icerockdev/moko-resources)
* [Kotlinx Resources](https://github.com/goncalossilva/kotlinx-resources)
* [Libres](https://github.com/skeptick/libres)
* [Kotlin JS Resources](https://gitlab.com/opensavvy/automation/kotlin-js-resources)
* [kMbed](https://github.com/karmakrafts/kMbed)
* [Compose Resources KMP](https://github.com/JavierSegoviaCordoba/compose-resources-kmp/tree/main)

## Installation

Both the Gradle plugin and the runtime library are published on MavenCentral.

Make sure MavenCentral is listed as a repository in the pluginManagement block of your *settings.gradle.kts*:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

To package assets as native resources and prepare them for use in your application, add the following Gradle plugins:

```kotlin
plugins {
    id("at.released.cassettes.rewrap") version "0.1-alpha01"
    id("at.released.cassettes.unwrap") version "0.1-alpha01"
}
```

To read assets at runtime, include the *cassettes-playhead* library:

```kotlin
dependencies {
    implementation("at.released.cassettes:cassettes-playhead:0.1-alpha01")
}
```

### Snapshot versions

Snapshot versions of the library may be published to a self-hosted public repository.

```kotlin
pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.pixnews.ru")
            mavenContent {
                includeGroupAndSubgroups("at.released.cassettes")
            }
        }
    }
}
```

## Usage

To add files as assets, use the the *cassettes* block in your Gradle configuration:

```kotlin
plugins {
    id("at.released.cassettes.plugin.rewrap")
}

cassettes {
    files.from(
        configurations.findByName("wasmSqliteReleaseElements")?.artifacts?.files
    )

    targetPackage = "ru.pixnews.wasm.sqlite.binary"
}
```

The *rewrap* plugin adds assets into
* Android projects (as assets in AAR files).
* JVM projects (as Java resources).
* Native targets (as separate published artifacts).

To read packaged assets, add the *unwrap* plugin and use the *cassettes-playhead* library:

__build.gradle.kts__

```kotlin
plugins {
    id("at.released.cassettes.plugin.unwrap")
}

dependencies {
    implementation("at.released.cassettes:cassettes-playhead:0.1-alpha01")
}
```

__app.kt__

```kotlin
import at.released.cassettes.playhead.AssetManager
import at.released.cassettes.playhead.AssetManager.readBytesOrThrow


AssetManager.readBytesOrThrow(AssetUrl("…"))
```

Currently, *AssetUrl* needs to be created manually

## Contributing

Any type of contributions are welcome. Please see the [contribution guide].

## License

These services are licensed under Apache 2.0 License. Authors and contributors are listed in the
[Authors] file.

```
Copyright 2025 cassettes-kmp project authors and contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[Authors]: https://github.com/illarionov/cassettes-kmp/blob/main/AUTHORS
[Compose Multiplatform Resources]: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources.html
[contribution guide]: https://github.com/illarionov/cassettes-kmp/blob/main/CONTRIBUTING.md
