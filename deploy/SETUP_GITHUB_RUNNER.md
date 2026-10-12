# Deploy automático do Venus-CRUD na EC2

Este deploy mantém o container que já roda na EC2 e troca somente a imagem:

~~~text
GitHub Actions (build/test) -> GHCR :staging -> runner self-hosted na EC2
                                                       |
                                                       v
                                  docker pull/tag + systemd restart
                                                       |
                                                       v
                                             Nginx :80/:443
~~~

O serviço systemd existente continua sendo o responsável por iniciar o container, manter o arquivo de ambiente, as portas, os volumes e o restart automático. O workflow não copia JAR, não cria pasta para artefato e não coloca credenciais da aplicação no GitHub.

## 1. Instalar o runner na EC2

Faça esta etapa uma vez usando o AWS Systems Manager Session Manager, que já é o acesso administrativo usado para essa máquina.

No GitHub, abra:

Venus-System/Venus-CRUD -> Settings -> Actions -> Runners -> New self-hosted runner -> Linux -> x64

Copie os comandos de instalação exibidos pelo GitHub para a EC2. Eles já vêm com a versão atual do runner e o token temporário correto.

Recomenda-se instalar o runner como um usuário dedicado:

~~~bash
sudo useradd --create-home --shell /bin/bash github-runner || true
sudo mkdir -p /opt/actions-runner
sudo chown -R github-runner:github-runner /opt/actions-runner
sudo -iu github-runner
cd /opt/actions-runner
~~~

Durante o config.sh, use:

- nome: venus-crud-ec2;
- label adicional: venus-ec2;
- diretório de trabalho padrão: _work.

Depois, instale o runner como serviço e confira o estado:

~~~bash
sudo ./svc.sh install github-runner
sudo ./svc.sh start
sudo ./svc.sh status
~~~

O runner precisa conseguir executar docker, curl, systemctl e journalctl. O serviço do runner deve ficar ativo mesmo após reinicialização da EC2.

## 2. Instalar o helper de deploy na EC2

Depois que este PR for integrado ao develop, instale o script versionado como um arquivo pertencente ao root:

~~~bash
sudo curl --fail --location \
  https://raw.githubusercontent.com/Venus-System/Venus-CRUD/develop/deploy/venus-crud-deploy.sh \
  --output /usr/local/sbin/venus-crud-deploy
sudo chown root:root /usr/local/sbin/venus-crud-deploy
sudo chmod 0755 /usr/local/sbin/venus-crud-deploy
~~~

O usuário do runner só deve poder executar este helper com sudo, sem senha:

~~~bash
echo 'github-runner ALL=(root) NOPASSWD: /usr/local/sbin/venus-crud-deploy' | \
  sudo tee /etc/sudoers.d/venus-crud-deploy >/dev/null
sudo chmod 0440 /etc/sudoers.d/venus-crud-deploy
sudo visudo --check
~~~

O helper faz login temporário no GHCR usando o token da execução, baixa a imagem ghcr.io/venus-system/venus-crud:staging, atualiza a tag local usada pelo serviço e reinicia o systemd. O token é enviado via stdin e não fica salvo na EC2.

## 3. Conferir o serviço Docker existente

Antes do primeiro deploy, confirme que o serviço atual usa a imagem local venus-crud:current (ou informe a tag real na variável VENUS_CRUD_LOCAL_IMAGE):

~~~bash
sudo systemctl cat venus-crud.service
sudo docker ps --filter name=venus-crud
~~~

O serviço deve continuar responsável pelo comando docker run, pelo arquivo de ambiente da aplicação e pelo mapeamento local, por exemplo 127.0.0.1:8080:8080. Não é necessário expor a porta 8080 no Security Group.

## 4. Configurar o Environment do GitHub

Crie o Environment qa em Settings -> Environments. Se desejado, limite o ambiente à branch develop e adicione uma aprovação manual antes do deploy.

O workflow usa estas Environment Variables; os defaults normalmente são suficientes:

| Variável | Default | Uso |
| --- | --- | --- |
| VENUS_CRUD_SERVICE | venus-crud.service | Unit systemd que reinicia o container |
| VENUS_CRUD_LOCAL_IMAGE | auto | Tag local usada pelo docker run, por exemplo venus-crud:current |
| VENUS_CRUD_HEALTH_URL | http://127.0.0.1:8080/v3/api-docs | Endpoint local usado para validar o restart |

O endereço remoto é definido pelo workflow como ghcr.io/venus-system/venus-crud:staging. Credenciais do banco, Cloudinary e JWT continuam somente nas variáveis do container na EC2.

## 5. Como o fluxo funciona

Cada push no develop:

1. roda ./mvnw verify -B no GitHub-hosted runner;
2. gera a imagem Docker e publica as tags staging e develop-<sha> no GHCR;
3. entrega o token temporário do GitHub ao runner da EC2 via stdin;
4. faz docker pull da imagem staging;
5. atualiza a tag local usada pelo serviço e reinicia a unit systemd;
6. consulta o health check por até dois minutos;
7. restaura a imagem anterior e reinicia o serviço se a aplicação não voltar a responder.

O deploy só acontece no push em develop, não em pull requests. Isso evita executar comandos privilegiados da EC2 a partir de código não revisado de um fork.

## 6. Cuidados de segurança

- Proteja a branch develop e mantenha a revisão por PR.
- Não use o runner self-hosted para jobs disparados por pull requests de forks.
- Não coloque tokens, senhas ou arquivos .env no repositório.
- Mantenha a porta 8080 fechada no Security Group; somente Nginx deve ficar exposto.
- Se o nome da unit ou a tag local da imagem mudar, atualize as Environment Variables antes do próximo deploy.
