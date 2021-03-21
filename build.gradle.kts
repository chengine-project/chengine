import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    `java-library`
//    `maven-publish`
    kotlin("jvm") version "1.4.30"
}

group = "io.chengine"
version = "1.0-ALPHA"

//publishing {
//    publications {
//        create<MavenPublication>("myLibrary") {
//            from(components["java"])
//        }
//    }
//}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

//java {
//    withSourcesJar()
//}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}