#!/bin/bash
# ============================================================
# XOYZ — First-time setup script
# ============================================================
# This script:
#   1. Checks for required tools (Java, Android SDK, ADB)
#   2. Downloads the Gradle wrapper JAR if missing
#   3. Creates local.properties
#   4. Makes gradlew executable
# ============================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

echo ""
echo "=============================="
echo "   XOYZ — Project Setup"
echo "=============================="
echo ""

ERRORS=0

# --- Check Java ---
echo -n "Checking Java... "
if command -v java &> /dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -1)
    echo -e "${GREEN}Found${NC}: $JAVA_VER"
else
    echo -e "${RED}NOT FOUND${NC}"
    echo "  → Install JDK 17+: sudo apt install openjdk-17-jdk"
    ERRORS=$((ERRORS + 1))
fi

# --- Check JAVA_HOME ---
echo -n "Checking JAVA_HOME... "
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}Set${NC}: $JAVA_HOME"
else
    echo -e "${YELLOW}NOT SET${NC} (may still work if java is on PATH)"
fi

# --- Check ANDROID_HOME ---
echo -n "Checking ANDROID_HOME... "
if [ -n "$ANDROID_HOME" ]; then
    echo -e "${GREEN}Set${NC}: $ANDROID_HOME"
elif [ -n "$ANDROID_SDK_ROOT" ]; then
    echo -e "${GREEN}Set (ANDROID_SDK_ROOT)${NC}: $ANDROID_SDK_ROOT"
    export ANDROID_HOME="$ANDROID_SDK_ROOT"
elif [ -d "$HOME/Android/Sdk" ]; then
    echo -e "${YELLOW}Found at ~/Android/Sdk${NC} (but env var not set)"
    echo "  → Add to ~/.zshrc: export ANDROID_HOME=\"\$HOME/Android/Sdk\""
    export ANDROID_HOME="$HOME/Android/Sdk"
else
    echo -e "${RED}NOT FOUND${NC}"
    echo "  → See docs/BUILD_GUIDE.md for installation instructions"
    ERRORS=$((ERRORS + 1))
fi

# --- Check ADB ---
echo -n "Checking ADB... "
if command -v adb &> /dev/null; then
    ADB_VER=$(adb --version 2>&1 | head -1)
    echo -e "${GREEN}Found${NC}: $ADB_VER"
else
    echo -e "${RED}NOT FOUND${NC}"
    echo "  → Install: sdkmanager --install platform-tools"
    ERRORS=$((ERRORS + 1))
fi

# --- Check Android SDK packages ---
echo -n "Checking Android SDK platforms... "
if [ -d "$ANDROID_HOME/platforms/android-34" ] 2>/dev/null; then
    echo -e "${GREEN}android-34 found${NC}"
else
    echo -e "${YELLOW}android-34 not found${NC}"
    echo "  → Install: sdkmanager --install 'platforms;android-34'"
fi

echo -n "Checking Android SDK build-tools... "
if [ -d "$ANDROID_HOME/build-tools/34.0.0" ] 2>/dev/null; then
    echo -e "${GREEN}build-tools 34.0.0 found${NC}"
else
    echo -e "${YELLOW}build-tools 34.0.0 not found${NC}"
    echo "  → Install: sdkmanager --install 'build-tools;34.0.0'"
fi

echo ""

# --- Gradle Wrapper JAR ---
WRAPPER_JAR="$PROJECT_DIR/gradle/wrapper/gradle-wrapper.jar"
echo -n "Checking Gradle wrapper JAR... "
if [ -f "$WRAPPER_JAR" ]; then
    echo -e "${GREEN}Found${NC}"
else
    echo -e "${YELLOW}Missing — downloading...${NC}"
    GRADLE_VER="8.4"
    WRAPPER_JAR_URL="https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VER}/gradle/wrapper/gradle-wrapper.jar"

    # Alternative: download from services.gradle.org
    # This URL points to a known good wrapper JAR
    WRAPPER_JAR_URL="https://github.com/nickeys/gradle-wrapper-jar/raw/main/gradle-wrapper.jar"

    # Most reliable: use gradle itself if available
    if command -v gradle &> /dev/null; then
        echo "  Using local gradle to generate wrapper..."
        gradle wrapper --gradle-version "$GRADLE_VER"
        echo -e "  ${GREEN}Wrapper generated via gradle${NC}"
    else
        echo "  Gradle not installed globally. Downloading wrapper JAR..."
        # Download a pre-built wrapper JAR from Gradle's distribution
        mkdir -p "$(dirname "$WRAPPER_JAR")"

        # Create a minimal temp build to get the wrapper
        TMPDIR=$(mktemp -d)
        cd "$TMPDIR"

        # Download gradle distribution and extract just the wrapper jar
        DIST_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VER}-bin.zip"
        echo "  Downloading Gradle ${GRADLE_VER} distribution..."
        if curl -fsSL -o gradle-dist.zip "$DIST_URL"; then
            unzip -q -j gradle-dist.zip "gradle-${GRADLE_VER}/lib/gradle-wrapper-*.jar" -d . 2>/dev/null || true

            # The wrapper jar is actually in the wrapper folder of any gradle project
            # Let's extract the whole thing and use its wrapper
            unzip -q gradle-dist.zip "gradle-${GRADLE_VER}/bin/*" "gradle-${GRADLE_VER}/lib/*" -d . 2>/dev/null || true

            if [ -f "gradle-${GRADLE_VER}/bin/gradle" ]; then
                chmod +x "gradle-${GRADLE_VER}/bin/gradle"
                cd "$PROJECT_DIR"
                "$TMPDIR/gradle-${GRADLE_VER}/bin/gradle" wrapper --gradle-version "$GRADLE_VER"
                echo -e "  ${GREEN}Wrapper generated successfully${NC}"
            else
                echo -e "  ${RED}Failed to extract gradle. Please install gradle manually:${NC}"
                echo "  → sudo apt install gradle  OR"
                echo "  → sdk install gradle ${GRADLE_VER}"
                ERRORS=$((ERRORS + 1))
            fi
        else
            echo -e "  ${RED}Failed to download Gradle distribution${NC}"
            echo "  → Install gradle manually: sudo apt install gradle"
            echo "  → Then run: gradle wrapper --gradle-version ${GRADLE_VER}"
            ERRORS=$((ERRORS + 1))
        fi

        rm -rf "$TMPDIR"
        cd "$PROJECT_DIR"
    fi
fi

# --- Make gradlew executable ---
if [ -f "$PROJECT_DIR/gradlew" ]; then
    chmod +x "$PROJECT_DIR/gradlew"
    echo -e "Made gradlew executable: ${GREEN}OK${NC}"
fi

# --- Create local.properties ---
LOCAL_PROPS="$PROJECT_DIR/local.properties"
echo -n "Checking local.properties... "
if [ -f "$LOCAL_PROPS" ]; then
    echo -e "${GREEN}Already exists${NC}"
else
    if [ -n "$ANDROID_HOME" ]; then
        echo "sdk.dir=$ANDROID_HOME" > "$LOCAL_PROPS"
        echo -e "${GREEN}Created${NC} (sdk.dir=$ANDROID_HOME)"
    else
        echo -e "${YELLOW}Skipped${NC} (ANDROID_HOME not set)"
        echo "  → Create manually: echo 'sdk.dir=/path/to/sdk' > local.properties"
    fi
fi

# --- Make scripts executable ---
chmod +x "$PROJECT_DIR/scripts/"*.sh 2>/dev/null || true

echo ""
echo "=============================="
if [ $ERRORS -eq 0 ]; then
    echo -e "  ${GREEN}Setup complete!${NC}"
    echo ""
    echo "  Next steps:"
    echo "    ./scripts/build.sh         # Build debug APK"
    echo "    ./scripts/install.sh       # Install to device"
else
    echo -e "  ${RED}Setup has $ERRORS error(s)${NC}"
    echo "  Fix the issues above and run this script again."
fi
echo "=============================="
echo ""
