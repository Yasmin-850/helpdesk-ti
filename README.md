# Helpdesk TI

Sistema acadêmico de gerenciamento de chamados de TI desenvolvido com Java 21, Spring Boot, Thymeleaf, JPA e H2.

## Funcionalidades
- Login com sessão e senhas BCrypt
- Perfis Administrador e Solicitante
- Dashboard e listagem de chamados
- Criar, visualizar e editar chamados
- Alterar status e excluir (Administrador)
- Busca e filtros por status/prioridade
- Persistência local com H2
- Tratamento de 404 e permissões de acesso

## Acessos de demonstração (ambiente local)
- Administrador: `admin@helpdesk.local` / `Admin@123`
- Solicitante: `usuario@helpdesk.local` / `Usuario@123`

Em hospedagem pública, defina as variáveis `HELPDESK_ADMIN_PASSWORD` e `HELPDESK_USER_PASSWORD` com senhas próprias.

## Executar
No Windows, na pasta do projeto:

```powershell
.\\mvnw.cmd spring-boot:run
```

Abra `http://localhost:8080`.
