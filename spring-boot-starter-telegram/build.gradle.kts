plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("spring-boot-starter-telegram") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.5.16")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.16")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    api(project(":spring"))
    api(project(":telegram"))
    api(project(":core"))

    implementation(kotlin("stdlib"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar>().configureEach {
    this.enabled = false
}

tasks.jar {
    archiveClassifier.set("")
    enabled = true
}

java {
    withSourcesJar()
}