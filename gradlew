#!/bin/sh
# Gradle wrapper script

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

DIRNAME=$(dirname "$0")
cd "$DIRNAME" || exit

GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
export GRADLE_USER_HOME

JAVA_HOME="${JAVA_HOME:-}"
JAVACMD="${JAVACMD:-java}"

if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS \
    -classpath "$DIRNAME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"
