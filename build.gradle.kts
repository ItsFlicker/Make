import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    id("io.izzel.taboolib") version "2.0.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

taboolib {
    description {
        dependencies {
            name("MythicMobs").optional(true)
            name("SX-Item").optional(true)
            name("Zaphkiel").optional(true)
        }
    }
    env {
        install(CHAT, CONFIGURATION, KETHER, NMS_UTIL, UI, EXPANSION_COMMAND_HELPER, BUKKIT_ALL)
    }
    version {
        taboolib = "6.1.0"
    }
    relocate("ink.ptms.um","ray.mintcat.make.um")
}

repositories {
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v12004:12004:universal")
    taboo("ink.ptms:um:1.0.1")
    compileOnly("ink.ptms:Zaphkiel:2.0.14")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}