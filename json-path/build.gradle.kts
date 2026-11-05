plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"
version = "0.5.3"

dependencies {
    implementation("com.googlecode.json-simple:json-simple") {
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.hamcrest:hamcrest-library")
    implementation("commons-lang:commons-lang")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
