package com.example.Banco_Magalu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO para saque", example = "{ \"numeroConta\": \"12345\", \"valor\": 100.00 }")
public class SaqueDto {

    @Schema(description = "Número da conta onde o saque será realizado",
            example = "12345", required = true)
    @NotNull(message = "O campo conta não pode ser nulo")
    private String numeroConta;

    @Schema(description = "Valor do saque a ser realizado. Deve ser positivo e maior que zero.",
            example = "100.00", required = true)
    @NotNull(message = "O campo valor não pode ser nulo")
    @Positive(message = "O campo valor deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    public SaqueDto(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

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
