#!/usr/bin/env bash
set -Eeuo pipefail

remote_image="${1:?Uso: venus-crud-deploy <remote-image> [service|auto] [local-image|auto] [health-url] [registry-user]}"
service="${2:-venus-crud.service}"
local_image="${3:-auto}"
health_url="${4:-http://127.0.0.1:8080/v3/api-docs}"
registry_user="${5:-github-actions}"

if [[ "$service" == "auto" ]]; then
  mapfile -t candidates < <(
    systemctl list-unit-files --type=service --no-legend |
      awk '{print $1}' |
      grep -Ei 'venus-crud|crud' || true
  )
  if [[ "${#candidates[@]}" -ne 1 ]]; then
    echo "Não foi possível identificar uma única unit do Venus-CRUD." >&2
    printf 'Units encontradas:\n%s\n' "${candidates[*]:-nenhuma}" >&2
    echo "Defina VENUS_CRUD_SERVICE nas variáveis do ambiente qa." >&2
    exit 3
  fi
  service="${candidates[0]}"
fi

[[ "$service" == *.service ]] || service="${service}.service"
systemctl cat "$service" >/dev/null

if [[ "$local_image" == "auto" ]]; then
  local_image="$({
    systemctl cat "$service" |
      sed -nE 's/.*docker[[:space:]]+run.*[[:space:]]([^[:space:]]+)[[:space:]]*$/\1/p' |
      tail -n1
  })"
fi

if [[ -z "$local_image" || "$local_image" == "auto" ]]; then
  echo "Não foi possível identificar a imagem local usada pela unit $service." >&2
  echo "Defina VENUS_CRUD_LOCAL_IMAGE nas variáveis do ambiente qa." >&2
  exit 4
fi

if [[ ! -t 0 ]]; then
  docker login ghcr.io --username "$registry_user" --password-stdin
else
  echo "O token do GHCR deve ser enviado pela entrada padrão." >&2
  exit 5
fi
trap 'docker logout ghcr.io >/dev/null 2>&1 || true' EXIT

previous_id="$(docker image inspect "$local_image" --format '{{.Id}}' 2>/dev/null || true)"
previous_image="${local_image}-previous"

if [[ -n "$previous_id" ]]; then
  docker tag "$previous_id" "$previous_image"
fi

docker pull "$remote_image"
docker tag "$remote_image" "$local_image"

rollback() {
  if [[ -n "$previous_id" ]]; then
    echo "Tentando rollback para a imagem anterior..." >&2
    docker tag "$previous_id" "$local_image"
    systemctl restart "$service" || true
  else
    echo "Não existe imagem anterior local para rollback." >&2
  fi
}

if ! systemctl restart "$service"; then
  echo "Falha ao reiniciar $service." >&2
  journalctl -u "$service" -n 100 --no-pager >&2 || true
  rollback
  exit 1
fi

for _ in $(seq 1 60); do
  if curl --fail --silent --show-error --max-time 3 "$health_url" >/dev/null 2>&1; then
    echo "Deploy concluído: service=$service image=$remote_image local=$local_image health=$health_url"
    exit 0
  fi
  sleep 2
done

echo "A aplicação não respondeu ao health check após o deploy." >&2
journalctl -u "$service" -n 100 --no-pager >&2 || true
rollback
exit 1
