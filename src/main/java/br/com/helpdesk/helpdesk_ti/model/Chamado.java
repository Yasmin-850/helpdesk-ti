package br.com.helpdesk.helpdesk_ti.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    @NotBlank(message = "O solicitante é obrigatório")
    private String solicitante;

    @NotBlank(message = "O setor é obrigatório")
    private String setor;

    private String prioridade;
    private String status;

    // Construtor vazio necessário para o JPA
    public Chamado() {
    }

    public Chamado(Long id, String titulo, String descricao, String solicitante,
                   String setor, String prioridade, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.setor = setor;
        this.prioridade = prioridade;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}