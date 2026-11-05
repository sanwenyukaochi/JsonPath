import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

description = "Java JsonPath implementation"
plugins {
    id("com.diffplug.spotless") version "8.11.0"
}

version = "0.5.4"

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
                targetExclude("**/.devcontainer/devcontainer.json")
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
            add("api", "org.hamcrest:hamcrest-library:3.0")
            add("implementation", "commons-lang:commons-lang:2.6")
        }
        add(
            "testImplementation",
            platform("org.junit:junit-bom:6.1.3"),
        )
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    if (rootProject.hasProperty("release")) {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
    }
}
