val publish_to_maven: String = project.property("publish_to_maven") as String
val publishToMaven = publish_to_maven.toBoolean()
val maven_name: String = project.property("maven_name") as String
val maven_url: String = project.property("maven_url") as String
val root_package: String = project.property("root_package") as String
val mod_id: String = project.property("mod_id") as String

plugins {
    `maven-publish`
}

if (publishToMaven) {
    require(maven_name.isNotEmpty()) { "maven_name is empty!" }
    require(maven_url.isNotEmpty()) { "maven_url is empty!" }

    publishing {
        repositories {
            maven {
                name = maven_name.replace("\\s".toRegex(), "")
                url = uri(maven_url)
                credentials(PasswordCredentials::class.java)
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"]) // Publish with standard artifacts
                groupId = root_package
                artifactId = mod_id

                // Custom artifact:
                // If you want to publish a different artifact to the one outputted when building normally
                // Create a different gradle task (Jar task), in extra.gradle.kts
                // Remove the "from components.java" line above
                // Add this line (change the task name):
                // artifacts task_name
            }
        }
    }
}
