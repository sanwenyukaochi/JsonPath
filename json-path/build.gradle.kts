plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.5.6"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    implementation("net.minidev:json-smart")
    testImplementation("org.hamcrest:hamcrest-library")
    implementation("commons-lang:commons-lang")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("tools.jackson.core:jackson-databind")
}
