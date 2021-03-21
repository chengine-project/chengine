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
        create<MavenPublication>("spring") {
            artifact("build/libs/spring-$version.jar") {
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
    api(project(":core"))
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