plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.8.2-SNAPSHOT"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    implementation("net.minidev:json-smart")
    testImplementation("org.hamcrest:hamcrest-library")
    implementation("org.apache.commons:commons-lang3")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("tools.jackson.core:jackson-databind")
}
