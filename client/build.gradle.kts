plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

group = "xyz.atrius"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    // Jetbrains Compose
    implementation(compose.desktop.currentOs)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "$group.MainKt"
    }
}