#!/bin/bash
# ============================================================
# XOYZ — Stream device logs filtered to the app
# ============================================================

echo "Streaming XOYZ logs (Ctrl+C to stop)..."
echo ""
adb logcat -s XOYZ:* AndroidRuntime:E *:F
