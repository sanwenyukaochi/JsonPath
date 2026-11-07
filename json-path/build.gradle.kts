plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.9.2-SNAPSHOT"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    implementation("net.minidev:json-smart")
    testImplementation("tools.jackson.core:jackson-databind")
}
