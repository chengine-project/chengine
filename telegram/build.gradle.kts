import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    maven
}

group = "io.chengine"
version = "0.1-ALPHA"

publishing {
    publications {
        create<MavenPublication>("telegram") {
            artifact("build/libs/telegram-$version.jar") {
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
    api("org.telegram:telegrambots:5.2.0")
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