plugins {
    `maven-publish`
    id("org.jetbrains.kotlin.multiplatform")
    id("at.released.cassettes.plugin.rewrap")
    id("at.released.cassettes.plugin.unwrap")
}

kotlin {
    linuxX64()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosX64()
    macosArm64()
}

group = "com.example"
version = "9999"

val conf = configurations.consumable("wasmSqliteReleaseElements") {
    outgoing {
        artifact(layout.projectDirectory.file("resource.txt.wasm"))
    }
}.get()

val testingRepository = project.layout.buildDirectory.dir("repo")

cassettes {
    files.setFrom(layout.projectDirectory.file("resource.txt.wasm"))
    targetPackage.set("com.example.test")
}

publishing {
    repositories {
        maven {
            name = "test"
            setUrl(testingRepository)
        }
    }
    publications.withType<MavenPublication>().all {
        version = "9999"
    }
}
