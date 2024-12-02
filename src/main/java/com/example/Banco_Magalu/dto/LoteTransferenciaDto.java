package com.example.Banco_Magalu.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO para realizar transferências em lote, contendo uma lista de transferências.",
        example = "[{\"contaOrigem\": \"12345\", \"contaDestino\": \"54321\", \"valor\": 200.00}, {\"contaOrigem\": \"67890\", \"contaDestino\": \"98765\", \"valor\": 150.00}]")
public class LoteTransferenciaDto {

    @ArraySchema(
            schema = @Schema(description = "Lista de transferências a serem realizadas.",
                    implementation = TransferenciaDto.class),
            minItems = 1
    )
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
