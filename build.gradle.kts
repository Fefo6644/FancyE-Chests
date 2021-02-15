plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.hierynomus.license-base") version "0.15.0"
}

defaultTasks("clean", "licenseMain", "shadowJar")

project.group = "com.github.fefo"
project.version = "3.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

sourceSets {
    main {
        java.srcDir("src/main/java")
        resources.srcDir("src/main/resources")
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(8)
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            expand("pluginVersion" to project.version)
        }
    }

    shadowJar {
        relocate("net.kyori", "com.github.fefo.fancyechests.lib.kyori")
        relocate("com.mojang.brigadier", "com.github.fefo.fancyechests.lib.brigadier")
    }
}

license {
    header = rootProject.file("license-header.txt")
    encoding = "UTF-8"

    mapping("java", "DOUBLESLASH_STYLE")

    ext["year"] = 2021
    ext["name"] = "Fefo6644"
    ext["email"] = "federico.lopez.1999@outlook.com"

    include("**/*.java")
}

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://jitpack.io/") }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    implementation("com.mojang:brigadier:1.0.17")
    implementation("net.kyori:adventure-api:4.4.0") {
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "org.jetbrains", module = "annotations")
    }
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT") {
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "org.jetbrains", module = "annotations")
    }
    compileOnly("org.jetbrains:annotations:20.1.0")
}
