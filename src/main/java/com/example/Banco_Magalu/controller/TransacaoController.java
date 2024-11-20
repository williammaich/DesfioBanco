package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transferencia")
public class TransacaoController {

    private final TransacaoService transacaoService;

    // Construtor para injeção de dependência do TransacaoService
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * Endpoint para realizar uma transferência entre contas.
     *
     * @param contaOrigem Número da conta de origem.
     * @param contaDestino Número da conta de destino.
     * @param valor Valor da transferência.
     * @return A transação realizada.
     */
    @PostMapping
    public ResponseEntity<Transacao> realizarTransferencia(
            @RequestParam String contaOrigem,
            @RequestParam String contaDestino,
            @RequestParam BigDecimal valor) {

        // Tenta realizar a transferência e retorna a resposta apropriada
        try {
            // Chama o serviço para realizar a transferência
            Transacao transacao = transacaoService.realizarTransferencia(contaOrigem, contaDestino, valor);

            // Retorna resposta OK com a transação realizada
            return ResponseEntity.ok(transacao);

        } catch (RuntimeException e) {
            // Retorna resposta de erro caso ocorra uma exceção (ex: saldo insuficiente)
            return ResponseEntity.badRequest().body(null);
        }
    }
}
