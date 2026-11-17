import org.gradle.plugins.signing.SigningExtension
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

    group = "io.github.chengine-project"
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

// Release staging is opt-in: ordinary builds never require credentials or signing keys.
subprojects {
    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().configureEach {
                pom.developers {
                    developer {
                        name.set("Chengine contributors")
                        url.set("https://github.com/chengine-project/chengine/graphs/contributors")
                    }
                }
            }
        }
        if (providers.gradleProperty("prepareCentral").orNull == "true") {
            val useLocalGpg = providers.gradleProperty("useLocalGpg").orNull == "true"
            val key = providers.environmentVariable("CHENGINE_SIGNING_KEY").orNull
            check(useLocalGpg || !key.isNullOrBlank()) { "Set CHENGINE_SIGNING_KEY or use -PuseLocalGpg=true" }
            val password = providers.environmentVariable("CHENGINE_SIGNING_PASSWORD").orNull
            val keyId = providers.environmentVariable("CHENGINE_SIGNING_KEY_ID").orNull
            val javadocJar = tasks.register<Jar>("centralJavadocJar") {
                archiveClassifier.set("javadoc")
                from(rootProject.file("verification/central-javadoc.html")) { rename { "index.html" } }
            }
            val publishing = extensions.getByType<PublishingExtension>()
            publishing.publications.withType<MavenPublication>().configureEach { artifact(javadocJar) }
            publishing.repositories.maven {
                name = "CentralStaging"
                url = uri(rootProject.layout.buildDirectory.dir("central-staging"))
            }
            pluginManager.apply("signing")
            extensions.configure<SigningExtension> {
                if (useLocalGpg) useGpgCmd()
                else if (keyId.isNullOrBlank()) useInMemoryPgpKeys(key, password)
                else useInMemoryPgpKeys(keyId, key, password)
                sign(publishing.publications)
            }
        }
    }
}
