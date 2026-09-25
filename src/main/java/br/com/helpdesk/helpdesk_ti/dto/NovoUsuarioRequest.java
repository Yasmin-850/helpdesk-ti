package br.com.helpdesk.helpdesk_ti.dto;

public record NovoUsuarioRequest(String nome, String email, String senha, String perfil, String setor) {}
