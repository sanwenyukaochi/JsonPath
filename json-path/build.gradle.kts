plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "2.1.0"

dependencies {
    compileOnly("tools.jackson.core:jackson-databind")
    compileOnly("com.google.code.gson:gson")
    compileOnly("org.json:json")
    implementation("net.minidev:json-smart")
    testImplementation("tools.jackson.core:jackson-databind")
    testImplementation("com.google.code.gson:gson")
    testImplementation("org.json:json")
}
