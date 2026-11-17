import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"

    id("org.springframework.boot") version "2.6.7" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
    testImplementation("io.mockk:mockk:1.12.0")
    implementation(kotlin("stdlib"))
}

allprojects {

    group = "io.chengine"
    version = "1.0.1-BETA"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
        incremental = false
    }
}

subprojects {
    tasks.withType<Test> { useJUnitPlatform() }

    repositories {
        mavenCentral()
    }
}