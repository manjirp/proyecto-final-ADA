plugins {
    kotlin("jvm") version "1.9.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("mysql:mysql-connector-java:8.0.33")
}

// Configuración de la aplicación
application {
    mainClass.set("MainKt")
}

// Configuración del compilador de Kotlin
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21" // Cambié esto a JVM 17, ya que Kotlin 1.9.0 es compatible con esta versión.
    }
}

