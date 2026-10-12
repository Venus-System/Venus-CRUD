#!/usr/bin/env bash
set -Eeuo pipefail

remote_image="${1:?Uso: venus-crud-deploy <remote-image> [registry-user]}"
registry_user="${2:-github-actions}"
service="venus-crud.service"
health_url="http://127.0.0.1:8080/swagger-ui.html"

systemctl cat "$service" >/dev/null

local_image="$(
systemctl cat "$service" |
sed -nE 's/.*docker[[:space:]]+run.*[[:space:]]([^[:space:]]+)[[:space:]]*$/\1/p' |
tail -n1
)"

if [[ -z "$local_image" ]]; then
  echo "Não foi possível identificar a imagem local usada pela unit $service." >&2
  echo "Confira o ExecStart do serviço e deixe a imagem como o último argumento do docker run." >&2
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
  if curl --fail --location --silent --show-error --max-time 3 "$health_url" >/dev/null 2>&1; then
    echo "Deploy concluído: service=$service image=$remote_image local=$local_image health=$health_url"
    exit 0
  fi
  sleep 2
done

echo "A aplicação não respondeu ao health check após o deploy." >&2
journalctl -u "$service" -n 100 --no-pager >&2 || true
rollback
exit 1
