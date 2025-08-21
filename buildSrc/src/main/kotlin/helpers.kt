import groovy.text.SimpleTemplateEngine
import org.gradle.api.GradleException
import org.gradle.api.Project

fun Project.propertyString(key: String): String {
    val value = findProperty(key)
    return if (value is String) {
        interpolate(value)
    } else {
        value.toString()
    }
}

fun Project.propertyBool(key: String): Boolean = propertyString(key).toBoolean()

fun Project.propertyStringList(key: String): List<String> = propertyStringList(key, " ")

fun Project.propertyStringList(
    key: String,
    delimit: String,
): List<String> = propertyString(key).split(delimit).filter { it.isNotEmpty() }

fun Project.assertProperty(propertyName: String) {
    val v = findProperty(propertyName)?.toString() ?: throw GradleException("Property $propertyName is not defined!")
    if (v.isEmpty()) {
        throw GradleException("Property $propertyName is empty!")
    }
}

fun Project.assertSubProperties(
    propertyName: String,
    vararg subPropertyNames: String,
) {
    assertProperty(propertyName)
    if (propertyBool(propertyName)) {
        subPropertyNames.forEach { assertProperty(it) }
    }
}

fun Project.setDefaultProperty(
    propertyName: String,
    warn: Boolean,
    defaultValue: Any?,
) {
    val v = findProperty(propertyName)?.toString()
    val exists = !v.isNullOrEmpty()

    if (!exists) {
        if (warn) {
            logger.warn("Property $propertyName is ${if (v == null) "not defined" else "empty"}!")
        }
        setProperty(propertyName, v)
    }
}

fun Project.interpolate(value: String): String {
    if (value.contains($$"${")) {
        return SimpleTemplateEngine()
            .createTemplate(value)
            .make(properties)
            .toString()
    }
    return value
}
