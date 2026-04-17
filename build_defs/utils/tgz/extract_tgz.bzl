def _extract_tgz_impl(ctx):
    out_dir = ctx.actions.declare_directory(ctx.label.name)

    ctx.actions.run_shell(
        inputs = [ctx.file.src],
        outputs = [out_dir],
        command = """
set -euo pipefail

TMP=$(mktemp -d)
tar -xzf {src} -C $TMP

mkdir -p {out}
cp -r $TMP/{subdir}/* {out}/
""".format(
            src = ctx.file.src.path,
            out = out_dir.path,
            subdir = ctx.attr.subdir,
        ),
    )

    return [DefaultInfo(files = depset([out_dir]))]

extract_tgz = rule(
    implementation = _extract_tgz_impl,
    attrs = {
        "src": attr.label(allow_single_file = True),
        "subdir": attr.string(),
    },
)
