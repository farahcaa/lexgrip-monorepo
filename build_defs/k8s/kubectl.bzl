def _kubectl_apply_all_impl(ctx):
    script = ctx.actions.declare_file(ctx.label.name + ".sh")

    script_content = """#!/usr/bin/env bash
set -euo pipefail

RAW_INPUTS=(
"""
    for f in ctx.files.srcs:
        script_content += '  "{}"\n'.format(f.short_path)

    script_content += """)

# 1. Flatten all inputs into a single list of exact files
ALL_FILES=()
for f in "${RAW_INPUTS[@]}"; do
    if [ -d "$f" ]; then
        # Use find -L to aggressively follow symlinks and extract physical files
        while IFS= read -r file; do
            ALL_FILES+=("$file")
        done < <(find -L "$f" -type f \\( -name "*.yaml" -o -name "*.yml" \\))
    elif [ -f "$f" ]; then
        ALL_FILES+=("$f")
    fi
done

# 2. First pass: Apply CRDs and wait
echo "\342\217\263 First pass: Applying Custom Resource Definitions..."
for f in "${ALL_FILES[@]}"; do
    if grep -q "kind: CustomResourceDefinition" "$f" 2>/dev/null; then
        echo "Applying CRD: $f..."
        kubectl apply --server-side -f "$f"

        echo "Waiting for CRD in $f to be established..."
        kubectl wait --for condition=established --timeout=60s -f "$f" || true
    fi
done

# 3. Second pass: Applying all resources
echo "\360\237\232\200 Second pass: Applying all resources..."
for f in "${ALL_FILES[@]}"; do
    echo "Applying $f..."
    kubectl apply --server-side -f "$f"
done

echo "\342\234\205 All manifests applied successfully!"
"""

    ctx.actions.write(
        output = script,
        content = script_content,
        is_executable = True,
    )

    return [
        DefaultInfo(
            executable = script,
            runfiles = ctx.runfiles(files = ctx.files.srcs),
        ),
    ]

kubectl_apply_all = rule(
    implementation = _kubectl_apply_all_impl,
    attrs = {
        "srcs": attr.label_list(
            allow_files = True,
            mandatory = True,
        ),
    },
    executable = True,
)
