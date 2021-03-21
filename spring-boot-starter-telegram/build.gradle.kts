import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    maven
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "io.chengine"
version = "0.1-ALPHA"

publishing {
    publications {
        create<MavenPublication>("spring-boot-starter-telegram") {
            artifact("build/libs/spring-boot-starter-telegram-$version.jar") {
                extension="jar"
            }
        }
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    api(project(":spring"))
    api(project(":telegram"))
    api(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<BootJar>().configureEach {
    this.enabled = false
}

tasks.jar {
    enabled = true
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}