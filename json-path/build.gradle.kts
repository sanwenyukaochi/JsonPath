plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "1.3.0-SNAPSHOT"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    compileOnly("com.google.code.gson:gson")
    implementation("net.minidev:json-smart")
    testImplementation("tools.jackson.core:jackson-databind")
    testImplementation("com.google.code.gson:gson")
}
