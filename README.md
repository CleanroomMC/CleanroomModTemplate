# CleanroomModTemplate

A mod development template for Cleanroom. Requires Cleanroom 0.7.0 or above. Uses [CleanroomGradle](https://github.com/CleanroomMC/CleanroomGradle) and [Conventions](https://github.com/CleanroomMC/Conventions).

## Getting Started

1. Set `root_package`, `mod_id` and `mod_name` in `gradle.properties`, and move the sources to match.
2. Set `conventions.license`, then run `./gradlew extractConventions applyLicenseHeader`.
3. Run `./gradlew runClient`.

Every other option is documented next to its property in `gradle.properties`.

## Versioning

The version is computed from git tags. Tag `1.0.0` on `main` to release it, `./gradlew -q printVersion` prints the current value.

## Tokens

`@{NAME}` markers in classes and resources are replaced at build time. `tokens.properties` declares them.
