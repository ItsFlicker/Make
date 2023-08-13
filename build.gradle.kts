plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

taboolib {
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-kether")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("expansion-command-helper")
    install("platform-bukkit")
    relocate("ink.ptms.um","ray.mintcat.make.um")
    classifier = null
    version = "6.0.12-9"
}

repositories {
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11200:11200")
    taboo("ink.ptms:um:1.0.0-beta-30")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
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