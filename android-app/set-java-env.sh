#!/bin/bash
# Helper script to set Java environment variables
# Source this in your shell: source ./set-java-env.sh

# Try to find Java 17+ automatically
if [ -d "/e/anurag/android studio/jbr" ]; then
    export JAVA_HOME="/e/anurag/android studio/jbr"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ Java environment set: $JAVA_HOME"
elif [ -d "/c/Program Files/Android/Android Studio/jbr" ]; then
    export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ Java environment set: $JAVA_HOME"
else
    echo "⚠️  Could not find Java installation. Please set JAVA_HOME manually."
    echo "   export JAVA_HOME=\"/path/to/java\""
fi
