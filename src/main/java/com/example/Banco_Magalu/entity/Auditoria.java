package com.example.Banco_Magalu.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Auditoria {

    @Id
    private UUID id;

    private String messagem;

    @ManyToOne
    @JoinColumn(name = "transacao_id", nullable = false)
    private Transacao transacao;

    public Auditoria() { }

    public Auditoria(UUID id, String messagem, Transacao transacao) {
        this.id = id;
        this.messagem = messagem;
        this.transacao = transacao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessagem() {
        return messagem;
    }

    public void setMessagem(String messagem) {
        this.messagem = messagem;
    }

    public Transacao getTransacao() {
        return transacao;
    }

    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
    }
}
