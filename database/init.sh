#!/bin/bash
set -euo pipefail
if [[ ! "$DB_PASSWORD" =~ ^[A-Za-z0-9_!@#%^+=.-]{16,128}$ ]]; then
  echo 'DB_PASSWORD must have 16-128 letters, digits, or _!@#%^+=.- (no quotes/spaces).' >&2
  exit 1
fi
export SQLCMDPASSWORD="$MSSQL_SA_PASSWORD"
/opt/mssql-tools18/bin/sqlcmd -S db -U sa -C -b -v APP_PASSWORD="$DB_PASSWORD" -i /bootstrap/schema.sql
