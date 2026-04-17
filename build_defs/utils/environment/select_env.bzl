def select_env(dev = None, staging = None, prod = None, default = None):
    """
    Generic environment selection wrapper rule.
    Returns a `select` block mapping `//build_flags:dev`, `//build_flags:staging`, and `//build_flags:prod` to the provided values.

    Args:
        dev: Value to return if `//build_flags:dev` is set.
        staging: Value to return if `//build_flags:staging` is set.
        prod: Value to return if `//build_flags:prod` is set.
        default: Default value for `//conditions:default`.

    Returns:
        A Bazel `select` block.
    """

    mapping = {}

    if dev != None:
        mapping["//build_flags:dev"] = dev
    if staging != None:
        mapping["//build_flags:staging"] = staging
    if prod != None:
        mapping["//build_flags:prod"] = prod

    if default != None:
        mapping["//conditions:default"] = default
    elif dev != None:
        # Fallback to dev if default is not specified but dev is.
        mapping["//conditions:default"] = dev

    return select(mapping)
