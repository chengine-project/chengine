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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    api(project(":spring"))
    api(project(":telegram"))
    api(project(":core"))

    implementation(kotlin("stdlib"))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar>().configureEach {
    this.enabled = false
}

tasks.jar {
    enabled = true
}

java {
    withSourcesJar()
}