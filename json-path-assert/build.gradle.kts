plugins {
    `java-library`
}

description = "json-path-assert"
version = "0.5.1-SNAPSHOT"

dependencies {
    api(project(":json-path"))
    implementation("com.googlecode.json-simple:json-simple")
    api("org.hamcrest:hamcrest-all")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
