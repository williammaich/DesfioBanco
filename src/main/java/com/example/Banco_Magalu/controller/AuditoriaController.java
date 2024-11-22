package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.service.AuditoriaService;
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
    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<List<String>> buscarMensagensAuditoriaConta(@PathVariable String numeroConta){
        List<String> mensagens = auditoriaService.buscarMensagensPorConta(numeroConta);
        return ResponseEntity.ok(mensagens);
    }
}
