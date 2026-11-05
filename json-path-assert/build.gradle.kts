plugins {
    `java-library`
}

description = "json-path-assert"
version = "0.5.3-SNAPSHOT"

dependencies {
    api(project(":json-path"))
    implementation("com.googlecode.json-simple:json-simple") {
        exclude(group = "junit", module = "junit")
    }
    api("org.hamcrest:hamcrest-library")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
