plugins {
    kotlin("jvm") version "2.4.21"
}

description = "Kotlin DSL for easy testing and reading of JSON data"
version = "0.0.1-SNAPSHOT"

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-databind")
    compileOnly("tools.jackson.core:jackson-databind")
    compileOnly("com.google.code.gson:gson")
    compileOnly("org.json:json")
    compileOnly("org.codehaus.jettison:jettison")
    compileOnly("org.apache.tapestry:tapestry-json")
    compileOnly("jakarta.json:jakarta.json-api")
    compileOnly("jakarta.json.bind:jakarta.json.bind-api")
    implementation("net.minidev:json-smart")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("tools.jackson.core:jackson-databind")
    testImplementation("com.google.code.gson:gson")
    testImplementation("org.json:json")
    testImplementation("org.codehaus.jettison:jettison")
    testImplementation("org.apache.tapestry:tapestry-json")
    testImplementation("jakarta.json:jakarta.json-api")
    testImplementation("jakarta.json.bind:jakarta.json.bind-api")
    testRuntimeOnly("org.eclipse.parsson:parsson")
    testRuntimeOnly("org.eclipse.yasson:yasson")
}
