plugins {
    kotlin("jvm")
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}

java {
    withSourcesJar()
}