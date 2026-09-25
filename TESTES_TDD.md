# Evidência de Testes Unitários e TDD - Helpdesk TI

## Objetivo

Demonstrar a aplicação de testes unitários sobre as regras de negócio e os principais componentes do sistema Helpdesk TI, conforme solicitado no Trabalho A3 de Gestão e Qualidade de Software.

## Ferramentas utilizadas

- JUnit 5
- Mockito
- Maven
- Spring Boot Test
- GitHub Actions para integração contínua e execução automática dos testes

## Modelo TDD utilizado

O ciclo adotado para demonstrar TDD é **Red -> Green -> Refactor**:

1. **Red:** criar ou executar um teste que inicialmente apresenta falha devido à ausência ou inadequação de determinado comportamento.
2. **Green:** implementar ou corrigir o código necessário para que o teste seja aprovado.
3. **Refactor:** melhorar a organização e a qualidade do código sem alterar seu comportamento, mantendo todos os testes aprovados.

Esse processo permite validar as funcionalidades durante a evolução do sistema e reduzir a possibilidade de regressões.

## Casos de teste implementados

### ChamadoServiceTest

Testes unitários da camada de serviço de chamados:

- Busca de chamado existente por ID.
- Retorno HTTP 404 quando o chamado não existe.
- Salvamento de chamado.
- Listagem de chamados.
- Busca de chamados por status.
- Busca de chamados por prioridade.
- Busca de chamados por setor.
- Busca de chamados por solicitante.
- Alteração do status de um chamado.
- Exclusão de chamado existente.
- Retorno de lista vazia quando não existem chamados.
- Utilização do Mockito para simular o repositório.

### ChamadoControllerTest

Testes das operações disponibilizadas pelo controlador de chamados:

- Listagem de chamados.
- Busca de chamado por ID.
- Criação de chamado.
- Busca por status.
- Busca por prioridade.
- Busca por setor.
- Busca por solicitante.
- Alteração do status de um chamado.
- Exclusão de chamado.
- Validação das operações considerando a sessão do usuário.
- Simulação de usuário administrador durante os testes.

### UsuarioServiceTest

Testes das principais regras relacionadas aos usuários:

- Bloqueio de criação de usuário com e-mail duplicado.
- Bloqueio da exclusão do próprio usuário conectado.
- Bloqueio da exclusão do último administrador.

## Integração Contínua - CI

O projeto utiliza **GitHub Actions** para realizar integração contínua.

O workflow configura o ambiente Java, utiliza Maven para realizar o build da aplicação e executa automaticamente os testes do projeto.

Dessa forma, novas alterações podem ser verificadas automaticamente, ajudando a identificar erros antes que sejam consideradas concluídas.

As execuções podem ser acompanhadas pela aba **Actions** do repositório no GitHub.

## Git Flow

O desenvolvimento utiliza branches para separar funcionalidades e organizar a evolução do sistema.

Entre as branches utilizadas no projeto estão:

- `main`
- `develop`
- `feature-alterar-status`
- `feature-busca-solicitante`
- `feature-busca-setor`
- `feature-busca-prioridade`

A utilização dessas branches permite separar o desenvolvimento de funcionalidades antes da integração com a branch principal.

## Commits semânticos

O projeto utiliza commits descritivos e passa a adotar o padrão de commits semânticos para facilitar a identificação das alterações realizadas.

Exemplos:

- `feat:` implementação de nova funcionalidade.
- `fix:` correção de problema.
- `test:` criação ou alteração de testes.
- `refactor:` melhoria ou reorganização interna do código.
- `docs:` criação ou atualização da documentação.

Exemplo utilizado na documentação:

`docs: atualiza documentação de testes e TDD`

## Como executar os testes

No Windows, abra o terminal na pasta do projeto e execute:

```powershell
.\mvnw.cmd test
