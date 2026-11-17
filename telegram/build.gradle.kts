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
    api("org.telegram:telegrambots-longpolling:10.3.0")
    api("org.telegram:telegrambots-client:10.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

kotlin.sourceSets.getByName("test").kotlin.srcDirs("../examples/conversation", "../examples/inline-keyboard", "../examples/media", "../examples/polls")

java {
    withSourcesJar()
}