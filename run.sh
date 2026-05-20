#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

OUT_DIR="$ROOT_DIR/out"
mkdir -p "$OUT_DIR"

# Compile all sources
find "$ROOT_DIR/src" -name "*.java" > "$OUT_DIR/sources.txt"
javac -d "$OUT_DIR" -cp "$ROOT_DIR/lib/*" @"$OUT_DIR/sources.txt"

# Run the app
java -cp "$OUT_DIR:$ROOT_DIR/lib/*" Main
