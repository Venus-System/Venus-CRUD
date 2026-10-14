#!/usr/bin/env bash
set -Eeuo pipefail

remote_image="${1:?Uso: venus-crud-deploy <remote-image> [registry-user]}"
registry_user="${2:-github-actions}"
service="venus-crud.service"
container_name="venus-crud"
local_image="venus-crud:current"
previous_image="venus-crud:previous"
health_url="http://127.0.0.1:8080/swagger-ui.html"
state_dir="/var/lib/venus-crud"
failed_image_file="$state_dir/failed-image"
health_attempts=60
health_interval=2

allowed='^ghcr\.io/venus-system/venus-crud(:[A-Za-z0-9._-]+|@sha256:[0-9a-f]{64})$'
if [[ ! "$remote_image" =~ $allowed ]]; then
  echo "Imagem não permitida: $remote_image" >&2
  exit 2
fi

systemctl cat "$service" >/dev/null 2>&1 || {
  echo "A unit $service não existe ou não pôde ser lida." >&2
  exit 3
}

install -d -m 0755 "$state_dir"
previous_id="$(docker inspect --format '{{.Image}}' "$container_name" 2>/dev/null || true)"
failed_image_id="$(cat "$failed_image_file" 2>/dev/null || true)"
deployed_new_image=false
logged_in=false
new_image_id=""

cleanup() {
  if [[ "$logged_in" == true ]]; then
    docker logout ghcr.io >/dev/null 2>&1 || true
