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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")

    api("org.springframework:spring-context:5.3.20")
    api("com.github.ben-manes.caffeine:caffeine:3.1.0")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.mockk:mockk:1.12.4")

    implementation(kotlin("stdlib"))
}

java {
    withSourcesJar()
}