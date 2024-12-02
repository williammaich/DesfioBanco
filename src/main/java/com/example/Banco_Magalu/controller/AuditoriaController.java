package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /**
     * Endpoint para buscar mensagens de auditoria realacionada as contas
     * @param numeroConta numero da conta a ser buscada
     * @return Lista menagens de auditoria
     */
    @Operation(
            summary = "Buscar mensagens de auditoria por conta",
            description = "Retorna uma lista de mensagens de auditoria relacionadas a uma conta específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de mensagens de auditoria retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404",
                    description = "Conta não encontrada ou sem mensagens de auditoria.")
    })
    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<List<String>> buscarMensagensAuditoriaConta(@PathVariable String numeroConta){
        List<String> mensagens = auditoriaService.buscarMensagensPorConta(numeroConta);
        return ResponseEntity.ok(mensagens);
    }
}
