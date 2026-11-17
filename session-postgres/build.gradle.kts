plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":core"))
    runtimeOnly("org.postgresql:postgresql:42.7.8")
    testImplementation("org.postgresql:postgresql:42.7.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

java { withSourcesJar() }

publishing {
    publications {
        create<MavenPublication>("postgresSessionStore") { from(components["java"]) }
    }
}
