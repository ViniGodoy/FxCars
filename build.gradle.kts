plugins {
    application
    kotlin("jvm") version "2.2.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "cars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val kottest = "6.0.2"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:${kottest}")
    runtimeOnly("io.kotest:kotest-assertions-core:${kottest}")

}

javafx {
    version = "21.0.4"   // or 22 for JDK 22
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("cars.engine.FxWindow")
}
