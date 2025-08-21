pluginManagement {
    repositories {
        mavenCentral()
        maven {
            setUrl("https://maven.neoforged.net/releases")
        }
        maven {
            setUrl("https://maven.minecraftforge.net/")
        }
        maven {
            setUrl("https://maven.fabricmc.net/")
        }
        maven {
            setUrl("https://maven.wagyourtail.xyz/releases")
        }
        maven {
            setUrl("https://maven.wagyourtail.xyz/snapshots")
        }
        gradlePluginPortal {
            content {
                excludeGroup("org.apache.logging.log4j")
            }
        }
    }
}

plugins {
    // Automatic toolchain provisioning
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

// Due to an IntelliJ bug, this has to be done
// rootProject.name = archives_base_name
rootProject.name = rootProject.projectDir.name
