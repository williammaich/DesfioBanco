package com.example.Banco_Magalu.dto;

import java.util.List;

public class LoteTransferenciaDto {
    private List<TransferenciaDto> transferencias;

    public List<TransferenciaDto> getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(List<TransferenciaDto> transferencias) {
        this.transferencias = transferencias;
        }
}
