#!/usr/bin/env bash
set -euo pipefail

bazel build //services/keycloak/k8s/theme-image:keycloak-theme-image
