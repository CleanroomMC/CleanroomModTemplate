import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.2"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4.1"
    id("xyz.wagyourtail.unimined") version "1.4.10-kappa"
    id("net.kyori.blossom") version "2.2.0"
}

// Early Assertions
assertProperty("mod_version")
assertProperty("root_package")
assertProperty("mod_id")
assertProperty("mod_name")

assertSubProperties("use_access_transformer", "access_transformer_locations")
assertSubProperties("is_coremod", "coremod_includes_mod", "coremod_plugin_class_name")
assertSubProperties("use_asset_mover", "asset_mover_version")

setDefaultProperty("generate_sources_jar", true, false)
setDefaultProperty("generate_javadocs_jar", true, false)
setDefaultProperty("minecraft_username", true, "Developer")
setDefaultProperty("extra_jvm_args", false, "")

version = propertyString("mod_version")
group = propertyString("root_package")

base {
    archivesName.set(propertyString("mod_id"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    if (propertyBool("generate_sources_jar")) {
        withSourcesJar()
    }
    if (propertyBool("generate_javadocs_jar")) {
        withJavadocJar()
    }
}

kotlin {
    jvmToolchain(21)
}

configurations {
    val contain by creating
    implementation { extendsFrom(contain) }
    val modCompileOnly by creating
    compileOnly { extendsFrom(modCompileOnly) }
    val modRuntimeOnly by creating
    runtimeOnly { extendsFrom(modRuntimeOnly) }
}

val remapTaskName = if (propertyBool("enable_shadow")) "remapShadowJar" else "remapJar"

unimined.minecraft {
    version("1.12.2")

    mappings {
        mcp("stable", "39-1.12")
    }

    cleanroom {
        if (propertyBool("use_access_transformer")) {
            accessTransformer("${rootProject.projectDir}/src/main/resources/${propertyString("access_transformer_locations")}")
        }
        loader("0.4.4-alpha")
        runs.auth.username = property("minecraft_username").toString()
        runs.all {
            val extraArgs = propertyString("extra_jvm_args")
            if (extraArgs.trim().isNotEmpty()) {
                jvmArgs(extraArgs.split("\\s+"))
            }
            if (propertyBool("enable_foundation_debug")) {
                systemProperties.apply {
                    set("foundation.dump", "true")
                    set("foundation.verbose", "true")
                }
            }
            if (propertyBool("is_coremod")) {
                systemProperty("fml.coreMods.load", propertyString("coremod_plugin_class_name"))
            }
        }
    }

    defaultRemapJar = false

    val jarTaskName = if (propertyBool("enable_shadow")) "shadowJar" else "jar"

    remap(tasks.named(jarTaskName).get()) {
        mixinRemap {
            enableBaseMixin()
            enableMixinExtra()
            disableRefmap()
        }
    }

    mods {
        val modCompileOnly by configurations.getting
        val modRuntimeOnly by configurations.getting
        remap(modCompileOnly)
        remap(modRuntimeOnly)
    }
}

dependencies {
    if (propertyBool("use_asset_mover")) {
        implementation("com.cleanroommc:assetmover:${propertyString("asset_mover_version")}")
    }
    if (propertyBool("enable_junit_testing")) {
        testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

apply(plugin = "dependencies")

tasks.processResources {
    rename("(.+_at.cfg)", "META-INF/$1")
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("mod_id", propertyString("mod_id"))
                property("mod_name", propertyString("mod_name"))
                property("mod_version", propertyString("mod_version"))
                val rootPackage = propertyString("root_package")
                val modId = propertyString("mod_id")
                property("package", "$rootPackage.$modId")
            }
            resources {
                property("mod_id", propertyString("mod_id"))
                property("mod_name", propertyString("mod_name"))
                property("mod_version", propertyString("mod_version"))
                property("mod_description", propertyString("mod_description"))
                property("mod_authors", propertyStringList("mod_authors", ",").joinToString("\", \"") { it.trim() })
                property("mod_credits", propertyString("mod_credits"))
                property("mod_url", propertyString("mod_url"))
                property("mod_update_json", propertyString("mod_update_json"))
                property("mod_logo_path", propertyString("mod_logo_path"))
            }
        }
    }
}

if (!propertyBool("enable_shadow")) {
    tasks.shadowJar { enabled = false }
}

idea {
    module {
        inheritOutputDirs = true
    }
    project {
        settings {
            runConfigurations {
                add(Gradle("1. Build").apply {
                    setProperty("taskNames", listOf("build"))
                })
                add(Gradle("2. Run Client").apply {
                    setProperty("taskNames", listOf("runClient"))
                })
                add(Gradle("3. Run Server").apply {
                    setProperty("taskNames", listOf("runServer"))
                })
            }
            compiler.javac {
                afterEvaluate {
                    javacAdditionalOptions = "-encoding utf8"
                }
            }
        }
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val contain by configurations.getting
    if (!contain.isEmpty) {
        into("/") {
            from(contain)
        }
    }
    doFirst {
        manifest {
            val attributeMap = mutableMapOf<String, Any>()
            attributeMap["ModType"] = "CRL"
            if (!contain.isEmpty) {
                attributeMap["ContainedDeps"] = contain.joinToString(" ") { it.name }
                attributeMap["NonModDeps"] = true
            }
            if (propertyBool("is_coremod")) {
                attributeMap["FMLCorePlugin"] = propertyString("coremod_plugin_class_name")
                if (propertyBool("coremod_includes_mod")) {
                    attributeMap["FMLCorePluginContainsFMLMod"] = true
                }
            }
            if (propertyBool("use_access_transformer")) {
                attributeMap["FMLAT"] = propertyString("access_transformer_locations")
            }
            attributes(attributeMap)
        }
    }
    finalizedBy(tasks.named(remapTaskName).get())
}

tasks.shadowJar {
    configurations.add(project.configurations.shadow)
    archiveClassifier = "shadow"
}

tasks.named(remapTaskName) {
    doFirst {
        logging.captureStandardOutput(LogLevel.INFO)
    }
    doLast {
        logging.captureStandardOutput(LogLevel.QUIET)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
    javaLauncher =
        javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }

    if (propertyBool("show_testing_output")) {
        testLogging {
            showStandardStreams = true
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

apply(plugin = "publishing")
apply(plugin = "extra")
