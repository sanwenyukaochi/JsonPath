plugins {
    `java-library`
}

description = "json-path-assert"
version = "0.5.6-SNAPSHOT"

dependencies {
    api(project(":json-path"))
    implementation("net.minidev:json-smart")
    api("org.hamcrest:hamcrest-library")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
