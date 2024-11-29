package com.example.Banco_Magalu.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class LoteTransferenciaDto {
    private List<TransferenciaDto> transferencias;



    @JsonCreator
    public LoteTransferenciaDto(List<TransferenciaDto> transferencias) {
        this.transferencias = transferencias;
    }


    public List<TransferenciaDto> getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(List<TransferenciaDto> transferencias) {
        this.transferencias = transferencias;
    }
}
