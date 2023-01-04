plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
}

group = "net.azisaba"
version = "1.0.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://repo.acrylicstyle.xyz/repository/maven-public/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.Querz:NBT:5.5")
    implementation("xyz.acrylicstyle.java-util:common:1.2.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    compileOnly("org.jetbrains:annotations:23.1.0")
}

tasks {
    shadowJar {
        manifest {
            attributes(mapOf("Main-Class" to "net.azisaba.playerdataitemfinder.Main"))
        }
        archiveFileName.set("PlayerDataItemFinder.jar")
    }

    compileJava {
        options.encoding = "UTF-8"
    }
}
