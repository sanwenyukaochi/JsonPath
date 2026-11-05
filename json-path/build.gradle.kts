plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.5.2-SNAPSHOT"

dependencies {
    implementation("com.googlecode.json-simple:json-simple")
    testImplementation("org.hamcrest:hamcrest-all")
    implementation("commons-lang:commons-lang")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
