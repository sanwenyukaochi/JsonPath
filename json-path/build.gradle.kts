plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"

dependencies {
    implementation("com.googlecode.json-simple:json-simple")
    implementation("org.hamcrest:hamcrest-all")
    implementation("commons-lang:commons-lang")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("org.openjdk.nashorn:nashorn-core:15.7")
}
