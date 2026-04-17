#!/bin/bash
# Usage: ./extract_crds.sh input.yaml output.yaml
set -euo pipefail

input="$1"
output="$2"

# Separate YAML documents with '---', keep only CRDs
awk '
  BEGIN { keep=0; }
  /^---/ { if(keep) { print buf "---" } buf=""; keep=0; next }
  {
    buf = buf $0 "\n"
    if ($0 ~ /^kind: CustomResourceDefinition/) { keep=1 }
  }
  END { if(keep) { print buf } }
' "$input" > "$output"