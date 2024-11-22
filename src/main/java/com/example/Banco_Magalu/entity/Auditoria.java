package com.example.Banco_Magalu.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String mensagem;

    @ManyToOne
    @JoinColumn(name = "transacao_id", nullable = false)
    private Transacao transacao;

    public Auditoria() { }

    public Auditoria(UUID id, String mensagem, Transacao transacao) {
        this.id = id;
        this.mensagem = mensagem;
        this.transacao = transacao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Transacao getTransacao() {
        return transacao;
    }

    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
    }
}
