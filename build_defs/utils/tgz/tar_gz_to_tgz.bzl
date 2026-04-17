# build_defs/utils/tgz/tar_gz_to_tgz.bzl
def _tar_gz_to_tgz_impl(ctx):
    output = ctx.actions.declare_file(ctx.attr.name + ".tgz")
    ctx.actions.run_shell(
        inputs = [ctx.file.src],
        outputs = [output],
        command = "cp {src} {dst}".format(
            src = ctx.file.src.path,
            dst = output.path,
        ),
    )
    return [DefaultInfo(files = depset([output]))]

tar_gz_to_tgz = rule(
    implementation = _tar_gz_to_tgz_impl,
    attrs = {
        "src": attr.label(
            mandatory = True,
            allow_single_file = [".tar.gz"],
        ),
    },
)
