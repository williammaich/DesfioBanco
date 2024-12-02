package com.example.Banco_Magalu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "DTO para realizar uma transferência entre contas",
        example = "{ \"contaOrigem\": \"12345\", \"contaDestino\": \"54321\", \"valor\": 200.00 }")
public class TransferenciaDto {

    @Schema(description = "Número da conta de origem",
            example = "12345", required = true)
    @NotBlank(message = "Conta de origem não pode ser vazia")
    private String contaOrigem;

    @Schema(description = "Número da conta de destino",
            example = "54321", required = true)
    @NotBlank(message = "Conta de destino não pode ser vazia")
    private String contaDestino;

    @Schema(description = "Valor da transferência. Deve ser maior que zero.",
            example = "200.00", required = true)
    @NotNull(message = "Valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    public TransferenciaDto() {}

    public TransferenciaDto(String contaOrigem, String contaDestino, BigDecimal valor) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(String contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(String contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
