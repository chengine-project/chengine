import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"

    id("org.springframework.boot") version "3.5.16" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
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
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_17)
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
// Every published module carries the project's license and Maven metadata.
subprojects {
    tasks.withType<Jar>().configureEach {
        from(rootProject.file("LICENSE")) { into("META-INF") }
    }
    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().configureEach {
                pom {
                    name.set("Chengine ${project.name}")
                    description.set("Kotlin framework for building chatbots")
                    url.set("https://github.com/chengine-project/chengine")
                    licenses {
                        license {
                            name.set("The MIT License")
                            url.set("https://opensource.org/license/mit")
                        }
                    }
                    scm {
                        url.set("https://github.com/chengine-project/chengine")
                        connection.set("scm:git:https://github.com/chengine-project/chengine.git")
                    }
                }
            }
        }
    }
}
