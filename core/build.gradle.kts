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
    api("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    api("org.springframework:spring-context:5.3.10")
    api("com.github.ben-manes.caffeine:caffeine:3.0.4")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.0.0")
    api("org.apache.logging.log4j:log4j-api:2.14.1")
    api("org.apache.logging.log4j:log4j-core:2.14.1")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
    testImplementation("io.mockk:mockk:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}

java {
    withSourcesJar()
}