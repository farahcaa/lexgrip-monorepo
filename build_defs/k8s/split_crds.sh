#!/bin/bash
# Usage: ./split_crds.sh <input.yaml> <output_crds.yaml> <output_no_crds.yaml>
set -euo pipefail

input="$1"
crds="$2"
no_crds="$3"

# initialize temp buffers
buf=""
keep=0

# clear output files
> "$crds"
> "$no_crds"

while IFS= read -r line; do
  if [[ "$line" == "---"* ]]; then
    # end of document: flush buf
    if [[ "$keep" -eq 1 ]]; then
      echo -n "$buf" >> "$crds"
      echo "---" >> "$crds"
    else
      echo -n "$buf" >> "$no_crds"
      echo "---" >> "$no_crds"
    fi
    buf=""
    keep=0
  else
    buf+="$line"$'\n'
    if [[ "$line" =~ ^kind:\ CustomResourceDefinition ]]; then
      keep=1
    fi
  fi
done < "$input"

# flush last document
if [[ -n "$buf" ]]; then
  if [[ "$keep" -eq 1 ]]; then
    echo -n "$buf" >> "$crds"
  else
    echo -n "$buf" >> "$no_crds"
  fi
fi