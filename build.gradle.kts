plugins {
    java
}

group = "com.lucio73"
version = "1.0.0"
description = "Warns server admins when more than one permissions plugin is installed."

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // Provided by the server at runtime, so compileOnly - never shade this.
    // Pinned for reproducible builds; use "26.2.build.+" to always take the
    // newest 26.2 build instead.
    compileOnly("io.papermc.paper:paper-api:26.2.build.116-stable")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:all")
}

// Expands ${version} (and anything else declared here) inside plugin.yml,
// so the plugin version only ever needs updating in this file.
tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveBaseName = "PermConflictWatchdog"
    archiveClassifier = ""
}
