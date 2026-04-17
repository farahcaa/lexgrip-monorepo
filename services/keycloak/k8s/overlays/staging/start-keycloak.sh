#!/bin/bash
set -e

# Import realm if it doesn't exist yet
if [ ! -f /opt/keycloak/data/.realm-imported ]; then
    echo "Importing realm..."
    # Note: in production, we might want to perform an export/import differently 
    # but for now we'll stick to this for consistency.
    /opt/keycloak/bin/kc.sh import --file /opt/keycloak/realms/master-realm.json
    touch /opt/keycloak/data/.realm-imported
    echo "Realm imported successfully"
fi

if [ -f /opt/keycloak/data/.realm-imported ]; then
    echo "Realm already imported"
fi

# Start Keycloak (Production Mode)
# Needs to have hostname set in production for security and proper URL generation.
# We assume env vars are provided for configuration.
exec /opt/keycloak/bin/kc.sh start "$@"
