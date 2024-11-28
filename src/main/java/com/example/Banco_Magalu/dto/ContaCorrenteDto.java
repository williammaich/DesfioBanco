package com.example.Banco_Magalu.dto;

import java.math.BigDecimal;

public class ContaCorrenteDto {

    private String numero;
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
