plugins {
    `maven-publish`
}
setDefaultProperty("publish_to_maven", true, false)

if (propertyBool("publish_to_maven")) {
    assertProperty("maven_name")
    assertProperty("maven_url")
    publishing {
        repositories {
            maven {
                name = propertyString("maven_name").replace("\\s", "")
                setUrl(propertyString("maven_url"))
                credentials(PasswordCredentials::class.java)
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"]) // Publish with standard artifacts
                groupId = (propertyString("root_package")) // Publish with root package as maven group
                artifactId = (propertyString("mod_id")) // Publish artifacts with mod id as the artifact id

                // Custom artifact:
                // If you want to publish a different artifact to the one outputted when building normally
                // Create a different gradle task (Jar task), in extra.gradle
                // Remove the "from components.java" line above
                // Add this line (change the task name):
                // artifacts task_name
            }
        }
    }
}
