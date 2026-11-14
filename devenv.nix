{ pkgs, ... }:

{
  packages = [ pkgs.git ];

  languages.java = {
    enable = true;
    # Match conventions.javaMajor in gradle.properties.
    jdk.package = pkgs.jdk25;
  };

  languages.kotlin.enable = true;

  # Kotlin tooling also brings a JDK; keep java/javac aligned with JAVA_HOME.
  enterShell = ''
    export PATH="$JAVA_HOME/bin:$PATH"
  '';

  # Use ./gradlew for builds to respect the project's pinned Gradle version.
  enterTest = ''
    java -version
    javac -version
    kotlinc -version
    test "$JAVA_HOME" = "${pkgs.jdk25.home}"
    java -version 2>&1 | grep 'version "25\.'
    javac -version | grep '^javac 25\.'
  '';
}
