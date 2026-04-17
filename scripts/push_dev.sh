#!/bin/bash
set -euo pipefail

IMAGE_TAG="dev-$(git rev-parse --short HEAD)"

echo "Pushing images to GHCR with tag: $IMAGE_TAG"

# Build and push images
bazel run //services/app-platform:app-platform-push -- --tag=$IMAGE_TAG

# Inject image tags into kustomization files
echo "Injecting image tags into kustomization overlays..."

for service in app-platform; do
    overlay_path="services/$service/k8s/overlays/dev"

    # Use kustomize edit to set the image tag
    cd $overlay_path
    kustomize edit set image \
        localhost/farakov-engineering/$service=ghcr.io/farakov-engineering/vrelte/$service:$IMAGE_TAG
    cd -
done

# Generate manifests with updated image tags
echo "Generating dev manifests..."
bazel build //k8s:manifests --//build_flags:pipeline=dev

echo "Dev images pushed and manifests generated!"
