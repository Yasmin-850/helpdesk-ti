package br.com.helpdesk.helpdesk_ti.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String nome;
    @Email @NotBlank @Column(unique = true, nullable = false)
    private String email;
    @NotBlank private String senha;
    @NotBlank private String perfil; // ADMIN ou SOLICITANTE
    private String setor;

    public Usuario() {}
    public Usuario(String nome, String email, String senha, String perfil, String setor) {
        this.nome = nome; this.email = email; this.senha = senha; this.perfil = perfil; this.setor = setor;
    }
    public Long getId(){ return id; } public void setId(Long id){this.id=id;}
    public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getSenha(){return senha;} public void setSenha(String senha){this.senha=senha;}
    public String getPerfil(){return perfil;} public void setPerfil(String perfil){this.perfil=perfil;}
    public String getSetor(){return setor;} public void setSetor(String setor){this.setor=setor;}
}
