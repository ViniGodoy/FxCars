plugins {
    application
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "21.0.4"   // or 22 for JDK 22
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("cars.engine.FxWindow")
}
