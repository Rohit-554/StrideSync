#!/bin/bash
set -e

echo "Running common (KMP) tests on Desktop JVM..."
./gradlew :composeApp:desktopTest

echo "All tests passed."
