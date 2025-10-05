import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.0.2"
    id("xyz.wagyourtail.unimined") version "1.4.1"
    id("net.kyori.blossom") version "2.1.0"
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

configurations {
    val embed by creating
    val contain by creating
    implementation {
        extendsFrom(embed, contain)
    }
    val modCompileOnly by creating
    compileOnly { extendsFrom(modCompileOnly) }
    val modRuntimeOnly by creating
    runtimeOnly { extendsFrom(modRuntimeOnly) }
}

unimined.minecraft {
    version("1.12.2")

    mappings {
        mcp("stable", "39-1.12")
    }

    cleanroom {
        if (propertyBool("use_access_transformer")) {
            accessTransformer("${rootProject.projectDir}/src/main/resources/" + propertyString("access_transformer_locations"))
        }
        loader("0.3.19-alpha")
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

    if (propertyBool("enable_shadow")) {
        remap(tasks.shadowJar.get()) {
            mixinRemap {
                enableBaseMixin()
                enableMixinExtra()
                disableRefmap()
            }
        }
    } else {
        remap(tasks.jar.get()) {
            mixinRemap {
                enableBaseMixin()
                enableMixinExtra()
                disableRefmap()
            }
        }
    }

    mods {
        val modCompileOnly by configurations.getting
        remap(modCompileOnly)
    }
}

dependencies {
    if (propertyBool("use_asset_mover")) {
        implementation("com.cleanroommc:assetmover:${propertyString("asset_mover_version")}")
    }
    if (propertyBool("enable_junit_testing")) {
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

apply(plugin = "dependencies")

tasks.processResources {

    inputs.property("mod_id", propertyString("mod_id"))
    inputs.property("mod_name", propertyString("mod_name"))
    inputs.property("mod_version", propertyString("mod_version"))
    inputs.property("mod_description", propertyString("mod_description"))
    inputs.property("mod_authors", propertyStringList("mod_authors", ",").joinToString(", "))
    inputs.property("mod_credits", propertyString("mod_credits"))
    inputs.property("mod_url", propertyString("mod_url"))
    inputs.property("mod_update_json", propertyString("mod_update_json"))
    inputs.property("mod_logo_path", propertyString("mod_logo_path"))

    val filterList = listOf("mcmod.info", "pack.mcmeta")

    filesMatching(filterList) {
        expand(
            mapOf(
                "mod_id" to propertyString("mod_id"),
                "mod_name" to propertyString("mod_name"),
                "mod_version" to propertyString("mod_version"),
                "mod_description" to propertyString("mod_description"),
                "mod_authors" to propertyStringList("mod_authors", ",").joinToString(", "),
                "mod_credits" to propertyString("mod_credits"),
                "mod_url" to propertyString("mod_url"),
                "mod_update_json" to propertyString("mod_update_json"),
                "mod_logo_path" to propertyString("mod_logo_path"),
            ),
        )
    }

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
        }
    }
}

if (!propertyBool("enable_shadow")) {
    tasks.shadowJar { enabled = false }
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
    if (propertyBool("enable_shadow")) {
        finalizedBy(tasks.named("remapShadowJar"))
    } else {
        finalizedBy(tasks.named("remapJar"))
    }
}

tasks.shadowJar {
    configurations.add(project.configurations.shadow)
    archiveClassifier = "shadow"
}

tasks.named<RemapJarTask>("remapJar") {
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
