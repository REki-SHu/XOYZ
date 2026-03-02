#!/bin/bash
# ============================================================
# XOYZ — Clean build artifacts
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

echo "Cleaning build artifacts..."
./gradlew clean

echo ""
echo "✅ Clean complete"
