# Helpdesk TI

Sistema acadêmico de gerenciamento de chamados de TI desenvolvido com Java 21, Spring Boot, Thymeleaf, JPA e H2.

O projeto foi desenvolvido para o Trabalho A3 da disciplina de Gestão e Qualidade de Software, aplicando práticas de qualidade, testes automatizados, versionamento e integração contínua.

## Funcionalidades

- Login com sessão e senhas BCrypt
- Perfis Administrador e Solicitante
- Dashboard e listagem de chamados
- Criar, visualizar e editar chamados
- Alterar status dos chamados
- Exclusão de chamados pelo Administrador
- Busca e filtros por status e prioridade
- Gerenciamento de usuários
- Persistência local com H2
- Controle de permissões de acesso
- Tratamento global de erros

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Thymeleaf
- H2 Database
- Maven
- JUnit 5
- Mockito
- Git
- GitHub
- GitHub Actions

## Qualidade de Software

Durante o desenvolvimento foram aplicadas práticas estudadas na disciplina de Gestão e Qualidade de Software, incluindo:

- Clean Code
- Refatoração
- Testes unitários
- TDD (Test-Driven Development)
- Git Flow
- Commits semânticos
- Pull Requests
- Integração Contínua (CI) com GitHub Actions
- Tratamento de erros
- Controle de acesso por perfil de usuário

## Testes automatizados

O projeto possui testes automatizados utilizando JUnit 5 e Mockito.

Entre os componentes testados estão:

- ChamadoService
- ChamadoController
- UsuarioService

Os testes também são executados automaticamente pelo GitHub Actions durante o processo de integração contínua.

## Git Flow

O desenvolvimento utiliza branches para organizar a implementação e manutenção das funcionalidades.

Entre as branches utilizadas estão:

- `main`
- `develop`
- branches `feature/*`
- branches `refactor/*`

As alterações podem ser validadas por testes e Pull Requests antes da integração com a branch principal.

## ODS - Objetivos de Desenvolvimento Sustentável

### ODS 9 - Indústria, Inovação e Infraestrutura

O Helpdesk TI está associado à **ODS 9 - Indústria, Inovação e Infraestrutura**.

O sistema contribui para a organização e melhoria dos processos relacionados à infraestrutura de Tecnologia da Informação, permitindo registrar, acompanhar e gerenciar chamados técnicos de forma digital.

A utilização de um sistema centralizado de chamados auxilia na identificação de problemas tecnológicos, no acompanhamento dos atendimentos e na organização das solicitações dos usuários.

Dessa forma, o projeto demonstra como soluções de software podem apoiar processos de inovação e contribuir para uma infraestrutura tecnológica mais organizada e eficiente.

## Acessos de demonstração (ambiente local)

### Administrador

E-mail:

`admin@helpdesk.local`

Senha:

`Admin@123`

### Solicitante

E-mail:

`usuario@helpdesk.local`

Senha:

`Usuario@123`

Em hospedagem pública, defina as variáveis `HELPDESK_ADMIN_PASSWORD` e `HELPDESK_USER_PASSWORD` com senhas próprias.

## Como executar

No Windows, abra o terminal na pasta do projeto e execute:

```powershell
.\mvnw.cmd spring-boot:run
