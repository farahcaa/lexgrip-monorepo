#!/bin/bash
set -e

# Import realm if it doesn't exist yet
if [ ! -f /opt/keycloak/data/.realm-imported ]; then
    echo "Importing realm..."
    /opt/keycloak/bin/kc.sh import --file /opt/keycloak/realms/lexgrip.json --verbose
    touch /opt/keycloak/data/.realm-imported
    echo "Realm imported successfully"
fi

if [ -f /opt/keycloak/data/.realm-imported ]; then
    echo "Realm already imported"
fi

# Start Keycloak (Development Mode)
exec /opt/keycloak/bin/kc.sh start "$@" --optimized --verbose
