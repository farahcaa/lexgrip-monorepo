#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GHCR_ENV_FILE="${SCRIPT_DIR}/dev.ghcr.env"

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

# Configure GHCR pull secrets from local env file, if present.
if [ -f "${GHCR_ENV_FILE}" ]; then
    echo -e "${GREEN}Loading GHCR credentials from ${GHCR_ENV_FILE}...${NC}"
    set -a
    # shellcheck disable=SC1090
    source "${GHCR_ENV_FILE}"
    set +a

    : "${GHCR_USERNAME:?GHCR_USERNAME is required in scripts/dev.ghcr.env}"
    : "${GHCR_TOKEN:?GHCR_TOKEN is required in scripts/dev.ghcr.env}"

    GHCR_EMAIL="${GHCR_EMAIL:-}"
    GHCR_SERVER="${GHCR_SERVER:-ghcr.io}"
    K8S_NAMESPACE="${K8S_NAMESPACE:-default}"
    GHCR_SECRET_NAMES="${GHCR_SECRET_NAMES:-ghcr-secret registry-pull ghcr-pull}"

    echo -e "${GREEN}Creating/updating GHCR pull secret(s) in namespace ${K8S_NAMESPACE}...${NC}"
    for secret_name in ${GHCR_SECRET_NAMES}; do
        kubectl create secret docker-registry "${secret_name}" \
          --namespace "${K8S_NAMESPACE}" \
          --docker-server="${GHCR_SERVER}" \
          --docker-username="${GHCR_USERNAME}" \
          --docker-password="${GHCR_TOKEN}" \
          --docker-email="${GHCR_EMAIL}" \
          --dry-run=client -o yaml | kubectl apply -f -
    done
else
    echo -e "${YELLOW}No ${GHCR_ENV_FILE} found. Skipping GHCR secret setup.${NC}"
    echo -e "${YELLOW}Copy ${SCRIPT_DIR}/dev.ghcr.env.example to ${GHCR_ENV_FILE} to enable it.${NC}"
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
