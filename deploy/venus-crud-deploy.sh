#!/usr/bin/env bash
# shellcheck disable=SC2317
set -euo pipefail

remote_image="${1:?Uso: venus-crud-deploy <remote-image> [registry-user]}"
registry_user="${2:-github-actions}"

# Estes valores precisam corresponder à unit instalada na EC2.
service="venus-crud.service"
local_image="venus-crud:current"
health_url="http://127.0.0.1:8080/swagger-ui.html"
health_attempts=60
health_interval=2

allowed='^ghcr\.io/venus-system/venus-crud(:[A-Za-z0-9._-]+|@sha256:[0-9a-f]{64})$'
if [[ ! "$remote_image" =~ $allowed ]]; then
  echo "Imagem não permitida: $remote_image" >&2
  exit 2
fi

local_allowed='^[a-z0-9]+([._/-][a-z0-9]+)*(:[A-Za-z0-9._-]+)?$'
if [[ ! "$local_image" =~ $local_allowed ]]; then
  echo "Imagem local inválida: $local_image" >&2
  exit 4
fi

systemctl cat "$service" >/dev/null

cleanup() {
  docker logout ghcr.io >/dev/null 2>&1 || true
}
trap cleanup EXIT

if [[ -t 0 ]]; then
  echo "O token do GHCR deve ser enviado pela entrada padrão." >&2
  exit 5
fi

docker login ghcr.io --username "$registry_user" --password-stdin

previous_id="$(docker image inspect "$local_image" --format '{{.Id}}' 2>/dev/null || true)"
previous_image="${local_image}-previous"
deployed_new_image=false

if [[ -n "$previous_id" ]]; then
  docker tag "$previous_id" "$previous_image"
fi

wait_for_image() {
  local expected_image_id="$1"
  local last_error="nenhuma resposta do health check"
  local health_error
  local container_id
  local image_id
  local found

  for ((attempt = 1; attempt <= health_attempts; attempt++)); do
    if health_error="$(curl --fail --location --silent --show-error --max-time 3 -o /dev/null "$health_url" 2>&1)"; then
      found=false
      while IFS= read -r container_id; do
        [[ -z "$container_id" ]] && continue
        image_id="$(docker inspect --format '{{.Image}}' "$container_id" 2>/dev/null || true)"
        if [[ "$image_id" == "$expected_image_id" ]]; then
          found=true
          break
        fi
      done < <(docker ps -q)

      if [[ "$found" == true ]]; then
        return 0
      fi
      last_error="o health check respondeu, mas nenhum container ativo usa a imagem esperada"
    else
      last_error="${health_error:-nenhuma resposta do health check}"
    fi
    sleep "$health_interval"
  done

  echo "Health check falhou: $last_error" >&2
  return 1
}

rollback() {
  if [[ -z "$previous_id" ]]; then
    echo "Não existe imagem anterior local para rollback." >&2
    return 1
  fi

  echo "Tentando rollback para a imagem anterior..." >&2
  if ! docker tag "$previous_id" "$local_image"; then
    echo "Não foi possível restaurar a tag local anterior." >&2
    return 1
  fi
  if ! systemctl restart "$service"; then
    echo "Não foi possível reiniciar $service durante o rollback." >&2
    return 1
  fi
  if wait_for_image "$previous_id"; then
    echo "Rollback concluído com sucesso." >&2
    return 0
  fi

  echo "Rollback falhou: a imagem anterior não passou no health check." >&2
  return 1
}

handle_signal() {
  local signal="$1"
  local exit_code=130
  trap '' INT TERM
  if [[ "$signal" == TERM ]]; then
    exit_code=143
  fi

  echo "Recebido SIG$signal durante o deploy; revertendo sem aguardar health check." >&2
  if [[ "$deployed_new_image" == true && -n "$previous_id" ]]; then
    if docker tag "$previous_id" "$local_image" && systemctl restart "$service"; then
      echo "Rollback rápido concluído." >&2
    else
      echo "Rollback rápido falhou; o serviço pode estar indisponível." >&2
    fi
  fi
  exit "$exit_code"
}
trap 'handle_signal INT' INT
trap 'handle_signal TERM' TERM

docker pull "$remote_image"
docker tag "$remote_image" "$local_image"
deployed_new_image=true

new_image_id="$(docker image inspect "$local_image" --format '{{.Id}}')"
if [[ -z "$new_image_id" ]]; then
  echo "Não foi possível identificar a imagem nova após o pull." >&2
  rollback || true
  exit 1
fi

if ! systemctl restart "$service"; then
  echo "Falha ao reiniciar $service." >&2
  journalctl -u "$service" -n 100 --no-pager >&2 || true
  rollback || true
  exit 1
fi

if wait_for_image "$new_image_id"; then
  docker image prune -f >/dev/null || true
  deployed_new_image=false
  echo "Deploy concluído: service=$service image=$remote_image digest=$new_image_id local=$local_image health=$health_url"
  exit 0
fi

echo "A imagem nova não passou no health check." >&2
journalctl -u "$service" -n 100 --no-pager >&2 || true
if ! rollback; then
  echo "ATENÇÃO: o rollback também falhou; a aplicação pode estar indisponível." >&2
fi
exit 1
