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

    dependencies {
        constraints {
            add("implementation", "com.googlecode.json-simple:json-simple:1.1.1")
            add("api", "org.hamcrest:hamcrest-all:1.1")
            add("implementation", "commons-lang:commons-lang:2.5")
            add("implementation", "commons-jxpath:commons-jxpath:1.3")
        }
        add(
            "testImplementation",
            platform("org.junit:junit-bom:5.11.4")
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