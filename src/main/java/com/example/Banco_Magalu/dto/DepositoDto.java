package com.example.Banco_Magalu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "DTO para realizar depósito em uma conta", example = "{ \"numeroConta\": \"12345\", \"valor\": 200.00 }")
public class DepositoDto {

    @Schema(description = "Número da conta onde o depósito será realizado", example = "12345")
    @NotNull
    private String numeroConta;

    @Schema(description = "Valor a ser depositado", example = "200.00")
    @NotNull
    @Positive(message = "Valor do depósito deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor do depósito deve ser maior que zero")
    private BigDecimal valor;



    public DepositoDto(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }
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
