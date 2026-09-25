# Evidência de Testes Unitários e TDD - Helpdesk TI

## Objetivo

Demonstrar a aplicação de testes unitários sobre regras de negócio do Helpdesk TI, conforme solicitado no Trabalho A3 de Gestão e Qualidade de Software.

## Ferramentas

- JUnit 5
- Mockito
- Maven
- GitHub Actions para execução automática dos testes

## Modelo TDD utilizado

O ciclo adotado para demonstrar TDD é **Red -> Green -> Refactor**:

1. **Red:** definir o comportamento esperado em um teste e observar a falha antes da implementação/correção correspondente.
2. **Green:** implementar o comportamento mínimo necessário para o teste passar.
3. **Refactor:** melhorar a organização do código sem alterar o comportamento, mantendo todos os testes aprovados.

## Casos cobertos

### ChamadoServiceTest

- Busca de chamado existente por ID.
- Retorno HTTP 404 quando o chamado não existe.
- Salvamento de novo chamado.
- Alteração do status de um chamado.
- Exclusão de chamado existente.

### UsuarioServiceTest

- Bloqueio de criação de usuário com e-mail duplicado.
- Bloqueio da exclusão do próprio usuário conectado.
- Bloqueio da exclusão do último administrador.

## Como executar

No Windows, na pasta do projeto:

```powershell
.\\mvnw.cmd test
```

Para executar testes e validação completa do build:

```powershell
.\\mvnw.cmd clean verify
```

## CI

O arquivo `.github/workflows/ci.yml` executa `clean verify` automaticamente em pushes e pull requests direcionados às branches `main` e `develop`.

## Evidência recomendada para apresentação

Guardar uma captura de tela do terminal com `BUILD SUCCESS` e a quantidade de testes executados, além da tela verde do workflow **CI - Build e Testes** na aba Actions do GitHub.
