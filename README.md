# CleanroomModTemplate

A mod development template for Cleanroom. Requires Cleanroom 0.7.0 or above. Uses [CleanroomGradle](https://github.com/CleanroomMC/CleanroomGradle) and [Conventions](https://github.com/CleanroomMC/Conventions).

## Getting Started

1. Set `root_package`, `mod_id` and `mod_name` in `gradle.properties`, and move the sources to match.
2. Set `conventions.license`, then run `./gradlew extractConventions applyLicenseHeader`.
3. Run `./gradlew runClient`.

Every other option is documented next to its property in `gradle.properties`.

## Nix Development Environment

The shared `devenv.nix` configuration provides JDK 25 (matching `conventions.javaMajor`), Kotlin, and Java/Kotlin language servers.

```sh
nix develop --impure
./gradlew build
```

Use `./gradlew` rather than the system `gradle` to use the project's pinned version. The environment supplies Kotlin tooling; compiling Kotlin mod sources also requires configuring the Kotlin Gradle plugin and runtime dependencies in the project.

The same configuration is available with `devenv shell` and can be smoke-tested with `devenv test`. The CLI uses `devenv.lock`, while `nix develop` uses `flake.lock`.

When adding these files to Git, include `devenv.nix`, `flake.nix`, and both lock files: flakes only include Git-tracked files.

## Versioning

The version is computed from git tags. Tag `1.0.0` on `main` to release it, `./gradlew -q printVersion` prints the current value.

## Tokens

`@{NAME}` markers in classes and resources are replaced at build time. `tokens.properties` declares them.
