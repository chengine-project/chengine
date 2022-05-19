plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("core") {
            from(components["java"])
        }
    }
}

dependencies {
    api(project(":core"))
    api("org.telegram:telegrambots:5.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(kotlin("stdlib"))
}

java {
    withSourcesJar()
}