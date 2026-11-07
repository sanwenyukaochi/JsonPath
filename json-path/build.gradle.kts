plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.8.0"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    implementation("net.minidev:json-smart")
    testImplementation("org.hamcrest:hamcrest-library")
    implementation("commons-lang:commons-lang")
    implementation("commons-io:commons-io")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("tools.jackson.core:jackson-databind")
}
