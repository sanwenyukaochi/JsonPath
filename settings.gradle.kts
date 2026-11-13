plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "json-path-parent"

include("json-path", "json-path-assert", "json-path-kotlin")
