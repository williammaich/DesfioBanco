package com.example.Banco_Magalu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class DepositoDto {

    @NotNull
    private String numeroConta;

    @NotNull
    @Positive(message = "Valor do depósito deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor do depósito deve ser maior que zero")
    private BigDecimal valor;

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
