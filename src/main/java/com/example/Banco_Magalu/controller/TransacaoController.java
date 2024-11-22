package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.dto.DepositoDto;
import com.example.Banco_Magalu.dto.LoteTransferenciaDto;
import com.example.Banco_Magalu.dto.SaqueDto;
import com.example.Banco_Magalu.dto.TransferenciaDto;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    // Construtor para injeção de dependência do TransacaoService
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * EndPoint para depositar valor na conta.
     * @param depositoDto Número da conta e valor a ser depositado.
     * @return A transação realizada.
     */
    @PostMapping("/deposito")
    public ResponseEntity<Transacao> realizarDeposito(@Valid @RequestBody DepositoDto depositoDto) {
        Transacao transacao = transacaoService.realizarDeposito(depositoDto.getNumeroConta(), depositoDto.getValor());
        return ResponseEntity.ok(transacao);
    }

    /**
     * Endpoint para realizar saque da conta.
     * @param saqueDto DTO com número da conta e valor do saque.
     * @return A transação realizada.
     */
    @PostMapping("/saque")
    public ResponseEntity<Transacao> realizarSaque(@Valid @RequestBody SaqueDto saqueDto) {
        Transacao transacao = transacaoService.realizarSaque(saqueDto.getNumeroConta(), saqueDto.getValor());
        return ResponseEntity.ok(transacao);
    }

    /**
     * Endpoint para realizar uma transferência entre contas.
     * @param transferenciaDto DTO com os dados da transferência.
     * @return A transação realizada.
     */
    @PostMapping("/transferencia")
    public ResponseEntity<Transacao> realizarTransferencia(@Valid @RequestBody TransferenciaDto transferenciaDto) {
        Transacao transacao = transacaoService.realizarTransferencia(
                transferenciaDto.getContaOrigem(),
                transferenciaDto.getContaDestino(),
                transferenciaDto.getValor()
        );
        return ResponseEntity.ok(transacao);
    }

    /**
     * Endpoint para transação em lote.
     * @param loteTransferenciaDto DTO com as transferências a serem realizadas.
     * @return Uma lista de transferências bem-sucedidas e falhas.
     */
    @PostMapping("/lote")
    public ResponseEntity<Map<String, Object>> realizarTransferenciaEmLote(@RequestBody LoteTransferenciaDto loteTransferenciaDto) {
        List<TransferenciaDto> transferencias = loteTransferenciaDto.getTransferencias();

        // Listas para armazenar as transferências bem-sucedidas e falhas
        List<TransferenciaDto> transferenciasBemSucedidas = new ArrayList<>();
        List<String> transferenciasFalhas = new ArrayList<>();

        // Processa as transferências em paralelo
        List<CompletableFuture<Void>> futures = transferencias.stream()
                .map(transferencia -> CompletableFuture.runAsync(() -> {
                    try {
                        // Realiza a transferência
                        transacaoService.realizarTransferencia(
                                transferencia.getContaOrigem(),
                                transferencia.getContaDestino(),
                                transferencia.getValor()
                        );
                        synchronized (transferenciasBemSucedidas) {
                            transferenciasBemSucedidas.add(transferencia);
                        }
                    } catch (Exception e) {
                        synchronized (transferenciasFalhas) {
                            transferenciasFalhas.add("Erro na transferência de " +
                                    transferencia.getContaOrigem() + " para " +
                                    transferencia.getContaDestino() + ": " + e.getMessage());
                        }
                    }
                }))
                .collect(Collectors.toList());

        // Aguarda a execução de todas as transferências
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Retorna o resultado
        return ResponseEntity.ok(
                Map.of(
                        "sucesso", transferenciasBemSucedidas,
                        "falhas", transferenciasFalhas
                )
        );
    }
}

