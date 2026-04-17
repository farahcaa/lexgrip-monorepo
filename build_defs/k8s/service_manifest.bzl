# build_defs/k8s/service_manifest.bzl
load("//build_flags:flags.bzl", "PipelineProvider")

def _k8s_service_manifest_impl(ctx):
    out = ctx.actions.declare_file(ctx.label.name + ".yaml")
    env = ctx.attr.env_flag[PipelineProvider].type

    # explicitly check that Bazel globbed the files
    expected_path = "overlays/{}/".format(env)
    found = False
    for f in ctx.files.srcs:
        if expected_path in f.path and f.basename.lower().startswith("kustomization"):
            found = True
            break

    if not found:
        fail("Bazel did not stage the Kustomization file for '%s'. Check your native.glob()!" % env)

    # define the path to the target overlay within the Bazel package structure
    overlay_path = "{package}/k8s/overlays/{env}".format(
        package = ctx.label.package,
        env = env,
    )

    # idiomatic Execution:
    # stay inside the sandbox.
    # use --load-restrictor LoadRestrictionsNone so Kustomize will tolerate Bazel's symlinks.
    cmd = "kustomize build --load-restrictor LoadRestrictionsNone {overlay_path} > {out_path}".format(
        overlay_path = overlay_path,
        out_path = out.path,
    )

    ctx.actions.run_shell(
        outputs = [out],
        inputs = ctx.files.srcs,
        command = cmd,
        mnemonic = "KustomizeBuild",
        use_default_shell_env = True,
        progress_message = "Generating %s manifests for %s" % (env, ctx.label.name),
    )

    return [DefaultInfo(files = depset([out]))]

_k8s_service_manifest = rule(
    implementation = _k8s_service_manifest_impl,
    attrs = {
        "srcs": attr.label_list(allow_files = True),
        "env_flag": attr.label(
            default = "//build_flags:pipeline",
            providers = [PipelineProvider],
        ),
    },
)

def k8s_service_manifest(name, **kwargs):
    _k8s_service_manifest(
        name = name,
        srcs = native.glob(["k8s/**"]),
        **kwargs
    )
