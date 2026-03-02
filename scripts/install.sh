#!/bin/bash
# ============================================================
# XOYZ — Install APK to connected device
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

APK_PATH="build/outputs/apk/debug/xoyz-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    echo "   Run ./scripts/build.sh first"
    exit 1
fi

# Check for connected device
echo "Checking for connected devices..."
DEVICES=$(adb devices | grep -v "List" | grep -v "^$" | wc -l)

if [ "$DEVICES" -eq 0 ]; then
    echo "❌ No devices connected"
    echo ""
    echo "   Connect via USB:"
    echo "     1. Enable USB Debugging on your phone"
    echo "     2. Connect via USB cable"
    echo ""
    echo "   Or connect wirelessly (Android 11+):"
    echo "     1. Enable Wireless Debugging on your phone"
    echo "     2. adb pair <ip>:<port>    (use pairing code)"
    echo "     3. adb connect <ip>:<port>"
    exit 1
fi

echo "Installing XOYZ..."
adb install -r "$APK_PATH"

echo ""
echo "✅ Installed successfully!"
echo ""

# Optionally launch the app
read -p "Launch the app? [Y/n] " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]] || [[ -z $REPLY ]]; then
    adb shell am start -n com.xoyz.game/.ui.MainActivity
    echo "🚀 App launched!"
fi
