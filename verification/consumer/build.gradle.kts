plugins { application }

repositories {
    maven {
        url = uri(requireNotNull(System.getenv("CHENGINE_LOCAL_MAVEN")) { "Set CHENGINE_LOCAL_MAVEN" })
        if (System.getenv("CHENGINE_POM_ONLY") == "true") {
            metadataSources { mavenPom(); artifact(); ignoreGradleMetadataRedirection() }
        }
    }
    mavenCentral()
}

dependencies {
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.1-BETA")
    implementation("io.github.chengine-project:session-postgres:1.0.1-BETA")
}

application { mainClass.set("io.chengine.verification.Main") }
