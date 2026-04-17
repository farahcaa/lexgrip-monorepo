def _minikube_load_all_impl(ctx):
    script_content = "#!/usr/bin/env bash\n"
    script_content += "set -euo pipefail\n"

    # Load each image with its absolute path from workspace root
    for image in ctx.files.images:
        script_content += "echo \"Loading {short_path} into minikube...\"\n".format(short_path = image.short_path)
        script_content += "minikube image load \"{image_path}\"\n\n".format(
            image_path = image.short_path,
        )

    script_content += "echo \"✅ All images loaded!\"\n"

    script = ctx.actions.declare_file(ctx.label.name + ".sh")

    ctx.actions.write(
        output = script,
        content = script_content,
        is_executable = True,
    )

    # The runfiles will ensure the image files are available
    return [DefaultInfo(
        executable = script,
        runfiles = ctx.runfiles(files = ctx.files.images),
    )]

minikube_load_all = rule(
    implementation = _minikube_load_all_impl,
    attrs = {
        "images": attr.label_list(
            allow_files = [".tar"],
            mandatory = True,
            doc = "List of OCI tarball targets to load",
        ),
    },
    executable = True,
)
