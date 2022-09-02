import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "xyz.atrius"
version = "0.1"

val kotestVersion = "5.4.2"

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        // Arrow
        implementation(platform("io.arrow-kt:arrow-stack:1.1.2"))
        implementation("io.arrow-kt:arrow-core")
        // Test Dependencies
        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        implementation("io.kotest.extensions:kotest-assertions-arrow:1.2.5")
        implementation("io.mockk:mockk:1.12.7")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}