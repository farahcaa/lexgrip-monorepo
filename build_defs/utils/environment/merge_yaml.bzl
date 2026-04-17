def _merge_yaml_impl(ctx):
    output = ctx.outputs.out

    src_files = ctx.files.srcs

    ctx.actions.run_shell(
        inputs = src_files,
        outputs = [output],
        command = """
output_path="$1"
shift

# Concatenate inputs, guaranteeing a trailing newline between files
tmpfile="$(mktemp)"
> "$tmpfile"
for f in "$@"; do
  [ -s "$f" ] || continue
  cat "$f" >> "$tmpfile"
  last_char=$(tail -c1 "$f" 2>/dev/null || true)
  if [ "$last_char" != $'\\n' ]; then
    printf '\\n' >> "$tmpfile"
  fi
done

cat "$tmpfile" | grep -v "^\\[\\]$" | grep -v "^$" > "$output_path"
""",
        arguments = [output.path] + [f.path for f in src_files],
    )

merge_yaml = rule(
    implementation = _merge_yaml_impl,
    attrs = {
        "srcs": attr.label_list(allow_files = [".yaml"], mandatory = True),
        "out": attr.output(mandatory = True),
    },
    doc = "Merges multiple YAML files into a single one, ensuring trailing newlines.",
)
