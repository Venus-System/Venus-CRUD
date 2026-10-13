# Deploy do Venus-CRUD na EC2

Este documento descreve a configuração única necessária para o deploy da imagem do GHCR na EC2. O workflow de deploy só publica e instala imagens originadas de 'develop'.

## Limites de segurança

- O workflow de pull request ('ci.yml') usa somente 'ubuntu-latest'; ele nunca deve usar o runner da EC2.
- O workflow de deploy aceita somente 'push' em 'develop' ou execução manual escolhendo 'develop'. Os dois jobs também verificam a referência da branch.
- O job de deploy usa o environment 'qa'. Em Settings > Environments > qa, deixe apenas 'develop' como branch de deploy e habilite aprovação manual se quiser uma etapa extra.
- Em Settings > Actions > General, exija aprovação para todos os pull requests de colaboradores externos. O runner deve ser registrado neste repositório, não na organização.
- O helper aceita somente imagens 'ghcr.io/venus-system/venus-crud' com tag ou digest SHA-256. Ele é root-owned e não deve ser editável pelo usuário do runner.

## Runner self-hosted

Instale o runner no repositório Venus-System/Venus-CRUD, com o label adicional 'venus-ec2'. Não use esse runner em workflows disparados por 'pull_request' e não o registre como runner de organização.

O usuário do serviço do runner será chamado abaixo de 'github-runner'. Use o usuário e o diretório definidos na instalação da sua EC2.

Depois de instalar o runner, confirme que ele aparece online em Settings > Actions > Runners com os labels 'self-hosted', 'linux', 'x64' e 'venus-ec2'.

## Helper root-owned

Na cópia do repositório usada para instalar a versão revisada:

~~~bash
sudo install -o root -g root -m 0755 deploy/venus-crud-deploy.sh /usr/local/sbin/venus-crud-deploy
sudo chown root:root /usr/local/sbin/venus-crud-deploy
sudo chmod 0755 /usr/local/sbin/venus-crud-deploy
~~~

O helper usa estes valores fixos, que precisam corresponder à unit da EC2:

- service: 'venus-crud.service';
- imagem local: 'venus-crud:current';
- health check: 'http://127.0.0.1:8080/swagger-ui.html'.

A unit deve manter os volumes, variáveis e portas atuais, mas usar 'venus-crud:current' como último argumento do 'docker run'. O helper confirma que um container em execução com essa imagem respondeu ao health check; ele não considera suficiente o Swagger antigo continuar respondendo.

Se qualquer um desses nomes ou a porta mudar, atualize o helper e reinstale-o antes de fazer o merge.

## Sudoers

Crie um arquivo root-owned, por exemplo '/etc/sudoers.d/github-runner-venus-crud':

~~~text
github-runner ALL=(root) NOPASSWD: /usr/local/sbin/venus-crud-deploy
~~~

Valide e proteja o arquivo:

~~~bash
sudo chmod 0440 /etc/sudoers.d/github-runner-venus-crud
sudo visudo -cf /etc/sudoers.d/github-runner-venus-crud
~~~

A permissão aceita qualquer argumento porque o próprio helper valida estritamente a imagem remota permitida. Não dê ao runner sudo genérico, acesso ao Docker socket sem essa necessidade ou permissão de escrita no helper.

## Teste antes do merge

1. Confirme que o serviço atual usa 'venus-crud:current' e que o health check responde.
2. Reinstale o helper e confira a permissão root-owned.
3. Faça um teste controlado com uma imagem do próprio GHCR usando um token de leitura de pacote; nunca coloque token no repositório ou nos logs.
4. Confira no log do helper o digest da imagem implantada e o resultado do rollback, se houver falha.
5. Faça um push de teste em 'develop' somente depois que o runner e o helper estiverem prontos.

Depois de qualquer alteração em 'deploy/venus-crud-deploy.sh', reinstale o arquivo na EC2. O CI executa ShellCheck no script, mas não atualiza automaticamente o arquivo já instalado no host.

O deploy não usa chave AWS no GitHub. O job usa apenas o 'GITHUB_TOKEN' efêmero para ler o pacote privado do GHCR.
