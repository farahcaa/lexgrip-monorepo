#!/bin/bash
set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting local development environment...${NC}"

# Check Minikube status safely
if ! minikube status >/dev/null 2>&1; then
    echo -e "${YELLOW}Minikube cluster does not exist. Creating it...${NC}"
    NEEDS_START=true
elif ! minikube status | grep -q "Running"; then
    echo -e "${YELLOW}Minikube exists but is not running. Starting it...${NC}"
    NEEDS_START=true
else
    NEEDS_START=false
fi

if [ "$NEEDS_START" = true ]; then
    minikube start --driver=docker --container-runtime=cri-o \
      --insecure-registry="localhost:5001" \
      --insecure-registry="host.docker.internal:5001" \
      --insecure-registry="192.168.1.42:5001"
fi

# Build all services
echo -e "${GREEN}Building all services...${NC}"
bazel build //...

# Load images into minikube
echo -e "${GREEN}Loading images into Minikube...${NC}"
bazel run //k8s:load-all-images

# Apply dev manifests
echo -e "${GREEN}Applying dev manifests...${NC}"
bazel run //k8s:apply-all --//build_flags:pipeline=dev

# Get service URLs
echo -e "${GREEN}Service URLs:${NC}"
minikube service list

echo -e "${GREEN}Local development environment ready!${NC}"
echo -e "${YELLOW}Use 'minikube dashboard' to access Kubernetes dashboard${NC}"