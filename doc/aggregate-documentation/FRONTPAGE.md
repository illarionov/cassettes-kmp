# Cassettes KMP

Assets for Kotlin Multiplatform projects.

## Installation

The latest release is available on [Maven Central]. Add the dependency:

```kotlin
dependencies {
    implementation("at.released.cassettes:cassettes-sync:0.1")
}
```

Snapshot versions of the library may be published to a self-hosted public repository.

```kotlin
pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.pixnews.ru")
            mavenContent {
                includeGroupAndSubgroups("at.released.builder.emscripten")
            }
        }
    }
}
```

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
