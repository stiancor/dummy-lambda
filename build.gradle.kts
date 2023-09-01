import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.1")
    implementation("com.amazonaws:aws-java-sdk-lambda:1.12.472")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {
    val shadowJar by getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        archiveBaseName.set("dummy-lambda")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}
