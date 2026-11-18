plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":telegram"))
    api("ai.koog:koog-agents:1.3.0")
    testImplementation("ai.koog:agents-test:1.3.0")
    testImplementation("ai.koog:prompt-executor-openrouter-client:1.3.0")
    testImplementation(project(":spring-boot-starter-telegram"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

kotlin.sourceSets.getByName("test").kotlin.srcDir("../examples/koog-telegram")

tasks.register<JavaExec>("runKoogTelegramDemo") {
    description = "Run the experimental Koog Telegram example using environment variables."
    group = "application"
    dependsOn(tasks.testClasses)
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("example.koog.KoogTelegramDemoKt")
}
