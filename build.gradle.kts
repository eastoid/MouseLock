import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    }

group = "mogware.mouseLock"
version = "1"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("com.1stleg:jnativehook:2.1.0")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation("com.github.kwhat:jnativehook:2.2.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


tasks {
    withType<ShadowJar> {
        archiveFileName.set("MouseLockFatjar.jar")


        exclude("unwantedFile.txt")

        manifest {
            attributes["Main-Class"] = "mogware.MouseLockKt"
        }
    }
}
