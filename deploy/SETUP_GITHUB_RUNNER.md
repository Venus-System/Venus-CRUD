# Deploy automático do Venus-CRUD na EC2

Este deploy mantém a arquitetura atual:

~~~text
GitHub Actions (build/test) -> artifact JAR -> runner self-hosted na EC2
                                                     |
                                                     v
                                           systemd -> Venus-CRUD :8080
                                                     |
                                                     v
                                              Nginx :80/:443
~~~

A API continua ouvindo apenas localmente na porta `8080`; o acesso externo permanece pelo Nginx e pelo endereço usado pelo mobile, `https://34-192-194-32.sslip.io/`. Não é necessário abrir a porta `8080`, criar uma chave AWS ou armazenar credenciais AWS no GitHub.

## 1. Instalar o runner na EC2

Faça esta etapa uma vez usando o AWS Systems Manager Session Manager, que já é o acesso administrativo usado para essa máquina.

No GitHub, abra:

`Venus-System/Venus-CRUD` -> `Settings` -> `Actions` -> `Runners` -> `New self-hosted runner` -> `Linux` -> `x64`

Copie os comandos de instalação exibidos pelo GitHub para a EC2. Eles já vêm com a versão atual do runner e o token temporário correto.

Recomenda-se instalar o runner como um usuário dedicado:

~~~bash
sudo useradd --create-home --shell /bin/bash github-runner || true
sudo mkdir -p /opt/actions-runner
sudo chown -R github-runner:github-runner /opt/actions-runner
sudo -iu github-runner
cd /opt/actions-runner
~~~

Durante o `config.sh`, use:

- nome: `venus-crud-ec2`;
- label adicional: `venus-ec2`;
- diretório de trabalho padrão: `_work`.

Depois, instale o runner como serviço e confira o estado:

~~~bash
sudo ./svc.sh install github-runner
sudo ./svc.sh start
sudo ./svc.sh status
~~~

O runner precisa conseguir executar `java`, `curl`, `systemctl` e `journalctl`. O serviço do runner deve ficar ativo mesmo após reinicialização da EC2.

## 2. Instalar o helper de deploy na EC2

Depois que este PR for integrado ao `develop`, instale o script versionado como um arquivo root-owned:

~~~bash
sudo curl --fail --location https://raw.githubusercontent.com/Venus-System/Venus-CRUD/develop/deploy/venus-crud-deploy.sh --output /usr/local/sbin/venus-crud-deploy
sudo chown root:root /usr/local/sbin/venus-crud-deploy
sudo chmod 0755 /usr/local/sbin/venus-crud-deploy
~~~

O usuário do runner só deve poder executar este helper com `sudo`, sem senha:

~~~bash
echo 'github-runner ALL=(root) NOPASSWD: /usr/local/sbin/venus-crud-deploy' | sudo tee /etc/sudoers.d/venus-crud-deploy >/dev/null
sudo chmod 0440 /etc/sudoers.d/venus-crud-deploy
sudo visudo --check
~~~

O helper identifica automaticamente a unit systemd e o caminho do JAR quando há exatamente uma unit cujo nome contém `venus` ou `crud`. Se houver mais de uma, configure os valores explicitamente na etapa seguinte.

## 3. Configurar o repositório

Crie o Environment `qa` em `Settings` -> `Environments`. Se desejado, limite o ambiente à branch `develop` e adicione uma aprovação manual antes do deploy.

O workflow usa estes Repository Variables; os defaults normalmente são suficientes:

| Variável | Default | Uso |
| --- | --- | --- |
| `VENUS_CRUD_SERVICE` | `auto` | Unit systemd, por exemplo `venus-crud.service` |
| `VENUS_CRUD_JAR_PATH` | `auto` | Caminho absoluto do JAR, por exemplo `/opt/venus-crud/venus-crud.jar` |
| `VENUS_CRUD_HEALTH_URL` | `http://127.0.0.1:8080/v3/api-docs` | Endpoint local usado para validar o restart |

Esses valores não são segredos. Credenciais do banco, Cloudinary e JWT continuam somente nas variáveis de ambiente da EC2.

## 4. Como o fluxo funciona

Cada push no `develop`:

1. roda `./mvnw verify -B` no GitHub-hosted runner;
2. gera a imagem Docker e publica as tags existentes no GHCR;
3. guarda o JAR aprovado como artifact da execução;
4. transfere o artifact para o runner self-hosted da EC2;
5. faz backup do JAR atual, instala o novo e reinicia a unit systemd;
6. consulta o health check por até dois minutos;
7. restaura o JAR anterior se a aplicação não voltar a responder.

O deploy só acontece no `push` em `develop`, não em pull requests. Isso evita executar comandos privilegiados da EC2 a partir de código não revisado de um fork.

## 5. Cuidados de segurança

- Proteja a branch `develop` e mantenha a revisão por PR.
- Não use o runner self-hosted para jobs disparados por pull requests de forks.
- Não coloque tokens, senhas ou arquivos `.env` no repositório.
- Mantenha a porta `8080` fechada no Security Group; somente Nginx deve ficar exposto.
- Se o nome da unit ou o caminho do JAR mudar, atualize as Repository Variables antes do próximo deploy.
