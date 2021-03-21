import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    maven
    `java-library`
    `maven-publish`
}

group = "io.chengine"
version = "0.1-ALPHA"

publishing {
    publications {
        create<MavenPublication>("core") {
            artifact("build/libs/core-$version.jar") {
                extension="jar"
            }
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
    api("org.springframework:spring-context:5.2.13.RELEASE")
    api("com.github.ben-manes.caffeine:caffeine:3.0.0")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.0.0")
    api("org.apache.logging.log4j:log4j-api:2.11.1")
    api("org.apache.logging.log4j:log4j-core:2.11.1")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}