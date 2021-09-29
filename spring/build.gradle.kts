plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("spring") {
            from(components["java"])
        }
    }
}

dependencies {
    api(project(":core"))
    implementation(kotlin("stdlib"))
}

java {
    withSourcesJar()
}