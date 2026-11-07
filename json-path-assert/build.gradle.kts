plugins {
    `java-library`
}

description = "json-path-assert"
version = "0.9.0-SNAPSHOT"

dependencies {
    api(project(":json-path"))
    api("org.hamcrest:hamcrest-library")
}
