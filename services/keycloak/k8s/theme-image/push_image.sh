#!/usr/bin/env bash
set -euo pipefail

IMAGE_TAG="${IMAGE_TAG:-26.5.6}"

bazel run //services/keycloak/k8s/theme-image:keycloak-theme-push -- --tag="${IMAGE_TAG}"
