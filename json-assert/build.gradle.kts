plugins {
    `java-library`
}

description = "Java DSL for easy testing and reading of JSON data"

dependencies {
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    api("org.hamcrest:hamcrest-all:1.1")
    implementation("commons-beanutils:commons-beanutils:1.8.3")
    implementation("commons-lang:commons-lang:2.5")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}