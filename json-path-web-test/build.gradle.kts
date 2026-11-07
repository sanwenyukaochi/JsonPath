import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    application
}

description = "JsonPath web demonstration and benchmark"
version = "2.6.0-SNAPSHOT"

application {
    mainClass.set("com.jayway.jsonpath.web.boot.Main")
    applicationDefaultJvmArgs = listOf("-Dorg.boon.faststringutils.disable=true")
}

dependencies {
    implementation(project(":json-path"))
    implementation("net.minidev:json-smart")
    implementation("tools.jackson.core:jackson-databind")
    implementation(platform("org.eclipse.jetty:jetty-bom:12.1.14"))
    implementation("org.eclipse.jetty:jetty-server")
    implementation("org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.14")
    implementation("org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.14")
    implementation(platform("org.glassfish.jersey:jersey-bom:4.0.3"))
    implementation("org.glassfish.jersey.containers:jersey-container-servlet")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson")
    runtimeOnly("org.glassfish.jersey.inject:jersey-hk2")
    implementation("commons-io:commons-io:2.22.0")
    implementation("io.fastjson:boon:0.34")
    implementation("com.nebhale.jsonpath:jsonpath:1.2")
    implementation("io.gatling:jsonpath_2.12:0.7.0")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.20")
}

val buildInfoDirectory = layout.buildDirectory.dir("generated/build-info")
val buildInfoVersion = version.toString()
val createBuildInfoFile by tasks.registering {
    outputs.dir(buildInfoDirectory)
    outputs.upToDateWhen { false }
    doLast {
        val buildInfoFile = buildInfoDirectory.get().file("build-info.properties").asFile
        buildInfoFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", buildInfoVersion)
        properties.setProperty("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
        buildInfoFile.writer().use { properties.store(it, null) }
    }
}

sourceSets.main {
    resources.srcDir(buildInfoDirectory)
}

tasks.processResources {
    dependsOn(createBuildInfoFile)
}
