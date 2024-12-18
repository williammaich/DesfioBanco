package com.example.Banco_Magalu.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ContaCorrente {


    @Id
    private String numero;

    private BigDecimal saldo;

    @Column(name = "limite_de_credito", nullable = false)
    private BigDecimal limiteCredito;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataDeCriacao;

    @Column(name = "limite_maximo")
    public BigDecimal limiteMaximo;

    @OneToMany(mappedBy = "contaCorrente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Transacao> transacoes = new ArrayList<>();

    public ContaCorrente() { }

    public ContaCorrente(String numero, BigDecimal saldo, BigDecimal limiteCredito, LocalDate dataDeCriacao,BigDecimal limiteMaximo, List<Transacao> transacoes) {
        this.numero = numero;
        this.saldo = saldo;
        this.limiteCredito = limiteCredito;
        this.dataDeCriacao = dataDeCriacao != null ? dataDeCriacao : LocalDate.now();
        this.transacoes = transacoes;
        this.limiteMaximo = limiteMaximo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public LocalDate getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(LocalDate dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }

    public BigDecimal getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(BigDecimal limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }
}
