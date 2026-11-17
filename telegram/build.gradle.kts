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
    api("org.telegram:telegrambots:6.9.7.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

kotlin.sourceSets.getByName("test").kotlin.srcDir("../examples/conversation")

java {
    withSourcesJar()
}