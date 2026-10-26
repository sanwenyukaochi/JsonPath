//plugins {
//    java
//    id("org.springframework.boot") version "4.1.2"
//    id("io.spring.dependency-management") version "1.1.7"
//}
//
//group = "com.gateway.llm"
//version = "0.0.1-SNAPSHOT"
//description = "json-path-parent"
//
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(27)
//    }
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("org.springframework.boot:spring-boot-starter")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}

import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

allprojects {
    group = "com.jayway.jsonassert"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnit()
    }

    if (rootProject.hasProperty("release")) {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
    }
}