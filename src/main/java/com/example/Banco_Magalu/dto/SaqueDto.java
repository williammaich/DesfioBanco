package com.example.Banco_Magalu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class SaqueDto {

    @NotNull(message = "O campo conta não pode ser nulo")
    private String numeroConta;

    @NotNull(message = "O campo valor não pode ser nulo")
    @Positive(message = "O campo valor deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumero(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
