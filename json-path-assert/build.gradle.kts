plugins {
    `java-library`
}

description = "json-path-assert"
version = "2.6.0-SNAPSHOT"

dependencies {
    api(project(":json-path"))
    api("org.hamcrest:hamcrest-library")
    compileOnly("tools.jackson.core:jackson-databind")
    testImplementation("tools.jackson.core:jackson-databind")
    testImplementation("net.minidev:json-smart")
}
