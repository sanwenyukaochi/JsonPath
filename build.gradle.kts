import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

description = "Java JsonPath implementation"
plugins {
    id("com.diffplug.spotless") version "8.11.0"
}

version = "3.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    group = "com.jayway.jsonpath"
    pluginManager.apply("com.diffplug.spotless")

    pluginManager.withPlugin("com.diffplug.spotless") {
        extensions.configure<SpotlessExtension> {
            encoding("UTF-8")

            java {
                target("**/*.java")
                targetExclude("**/local/**")
                forbidWildcardImports()
                forbidModuleImports()
                googleJavaFormat()
                    .aosp()
                    .reflowLongStrings(false)
                    .formatJavadoc(true)
                    .reorderImports(true)
                importOrder()
                removeUnusedImports()
                formatAnnotations()
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            yaml {
                target("**/*.yaml")
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            kotlin {
                target("**/*.kt")
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            kotlinGradle {
                target("**/*.gradle.kts")
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            gherkin {
                target("**/*.feature")
                gherkinUtils()
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            toml {
                target("**/*.toml")
                versionCatalog()
                    .stripQuotedKeys(true)
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }

            json {
                target("**/*.json")
                targetExclude(
                    "**/.devcontainer/devcontainer.json",
                    rootProject.file("json-path-assert/src/test/resources/books.json"),
                    rootProject.file("json-path-assert/src/test/resources/example.json"),
                    rootProject.file("json-path-assert/src/test/resources/invalid.json"),
                )
                gson()
                    .indentWithSpaces(4)
                    .sortByKeys()
                    .escapeHtml()
                trimTrailingWhitespace()
                endWithNewline()
                toggleOffOn()
            }
        }
    }
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

    dependencies {
        constraints {
            add("implementation", "net.minidev:json-smart:2.6.1")
            add("implementation", "com.google.code.gson:gson:2.15.0")
            add("implementation", "org.json:json:20260814")
            add("implementation", "org.codehaus.jettison:jettison:1.5.7")
            add("implementation", "org.apache.tapestry:tapestry-json:5.9.2")
            add("implementation", "jakarta.json:jakarta.json-api:2.1.3")
            add("implementation", "jakarta.json.bind:jakarta.json.bind-api:3.0.4")
            add("implementation", "org.eclipse.parsson:parsson:1.1.9")
            add("implementation", "org.eclipse.yasson:yasson:3.0.5")
            add("implementation", "tools.jackson.core:jackson-databind:3.2.4")
            add("api", "org.hamcrest:hamcrest-library:3.0")
            add("testImplementation", "org.assertj:assertj-core:3.27.7")
            add("testImplementation", "commons-io:commons-io:2.22.0")
            add("implementation", "org.slf4j:slf4j-api:2.0.20")
            add("testImplementation", "org.slf4j:slf4j-simple:2.0.20")
        }
        add(
            "testImplementation",
            platform("org.junit:junit-bom:6.1.3"),
        )
        add("testImplementation", "commons-io:commons-io")
        add("implementation", "org.slf4j:slf4j-api")
        add("testImplementation", "org.slf4j:slf4j-simple")
        add("testImplementation", "org.hamcrest:hamcrest-library")
        add("testImplementation", "org.assertj:assertj-core")
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
    }

    if (rootProject.hasProperty("release")) {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
    }
}
