plugins {
    `java-library`
}

description = "Executable BDD specifications for JsonPath"
version = "3.0.0"

dependencies {
    testImplementation(project(":json-path"))
    testImplementation(project(":json-path-assert"))
    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("org.junit.platform:junit-platform-suite")
}
