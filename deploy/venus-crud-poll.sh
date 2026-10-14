#!/usr/bin/env bash
set -Eeuo pipefail

remote_image="${VENUS_CRUD_REMOTE_IMAGE:-ghcr.io/venus-system/venus-crud:staging}"
registry_user="${VENUS_CRUD_REGISTRY_USER:-github-poller}"
token_file="${VENUS_CRUD_GHCR_TOKEN_FILE:-/etc/venus-crud-ghcr.token}"

if [[ ! -r "$token_file" || ! -s "$token_file" ]]; then
  echo "Token do GHCR não encontrado ou vazio: $token_file" >&2
  exit 1
fi

exec /usr/local/sbin/venus-crud-deploy "$remote_image" "$registry_user" < "$token_file"
