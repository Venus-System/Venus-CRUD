#!/usr/bin/env bash
set -Eeuo pipefail

artifact="${1:?Uso: venus-crud-deploy <jar> [service|auto] [jar-path|auto] [health-url]}"
service="${2:-auto}"
jar_path="${3:-auto}"
health_url="${4:-http://127.0.0.1:8080/v3/api-docs}"

if [[ ! -f "$artifact" || "$artifact" != *.jar ]]; then
  echo "Artefato JAR inválido: $artifact" >&2
  exit 2
fi

if [[ "$service" == "auto" ]]; then
  mapfile -t candidates < <(
    systemctl list-unit-files --type=service --no-legend |
      awk '{print $1}' |
      grep -Ei 'venus|crud' || true
  )
  if [[ "${#candidates[@]}" -ne 1 ]]; then
    echo "Não foi possível identificar uma única unit Venus-CRUD." >&2
    echo "Units encontradas: ${candidates[*]:-nenhuma}" >&2
    echo "Defina VENUS_CRUD_SERVICE nas variáveis do repositório." >&2
    exit 3
  fi
  service="${candidates[0]}"
fi

[[ "$service" == *.service ]] || service="${service}.service"
systemctl cat "$service" >/dev/null

if [[ "$jar_path" == "auto" ]]; then
  jar_path="$(
    systemctl cat "$service" |
      sed -nE 's/.*-jar[[:space:]]+([^[:space:]]+\.jar).*/\1/p' |
      tr -d "\"'" |
      head -n1
  )"
fi

if [[ -z "$jar_path" || "$jar_path" == "auto" || "$jar_path" != /* || "$jar_path" != *.jar ]]; then
  echo "Não foi possível identificar o caminho do JAR da unit $service." >&2
  echo "Defina VENUS_CRUD_JAR_PATH nas variáveis do repositório." >&2
  exit 4
fi

target_dir="$(dirname "$jar_path")"
previous="${jar_path}.previous"
next="${jar_path}.next"

install -d -m 0755 "$target_dir"

if [[ -f "$jar_path" ]]; then
  cp --preserve=mode,ownership "$jar_path" "$previous"
fi

install -m 0644 "$artifact" "$next"
mv -f "$next" "$jar_path"

systemctl restart "$service"

for _ in $(seq 1 60); do
  if curl --fail --silent --show-error --max-time 3 "$health_url" >/dev/null 2>&1; then
    echo "Deploy concluído: service=$service jar=$jar_path health=$health_url"
    exit 0
  fi
  sleep 2
done

echo "A aplicação não respondeu ao health check após o deploy." >&2
journalctl -u "$service" -n 100 --no-pager >&2 || true

if [[ -f "$previous" ]]; then
  echo "Tentando rollback para o JAR anterior..." >&2
  install -m 0644 "$previous" "$jar_path"
  systemctl restart "$service" || true
fi

exit 1
