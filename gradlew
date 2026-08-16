#!/bin/sh
exec "$JAVA_HOME/bin/java" \
  -classpath "$0/../gradle/wrapper/gradle-wrapper.jar" \
  org.gradle.wrapper.GradleWrapperMain "$@"
