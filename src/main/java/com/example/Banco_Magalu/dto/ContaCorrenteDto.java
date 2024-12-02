package com.example.Banco_Magalu.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO para representar os dados de uma conta corrente, incluindo número e saldo.")
public class ContaCorrenteDto {

    @Schema(description = "Número único da conta corrente.", example = "12345", required = true)
    private String numero;

    @Schema(description = "Saldo atual da conta corrente.", example = "500.00", required = true)
    private BigDecimal saldo;


    public ContaCorrenteDto(String numero, BigDecimal saldo) {
        this.numero = numero;
        this.saldo = saldo;
        }

        public String getNumero() {
            return numero;
        }

        public BigDecimal getSaldo() {
                return saldo;
        }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
