#!/bin/bash
# ============================================================
# XOYZ — Build debug APK
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

echo "Building XOYZ debug APK..."
echo ""

./gradlew assembleDebug

APK_PATH="build/outputs/apk/debug/xoyz-debug.apk"

if [ -f "$APK_PATH" ]; then
    SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "✅ Build successful!"
    echo "   APK: $APK_PATH ($SIZE)"
    echo ""
    echo "   Install with: ./scripts/install.sh"
else
    echo ""
    echo "❌ Build failed — APK not found"
    exit 1
fi
