#!/bin/bash
# ============================================================
# XOYZ — Build and install in one step
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

"$SCRIPT_DIR/build.sh"
echo ""
"$SCRIPT_DIR/install.sh"
