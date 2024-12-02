package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.dto.DepositoDto;
import com.example.Banco_Magalu.dto.LoteTransferenciaDto;
import com.example.Banco_Magalu.dto.SaqueDto;
import com.example.Banco_Magalu.dto.TransferenciaDto;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.service.TransacaoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * EndPoint para depositar valor na conta.
     * @param depositoDto Número da conta e valor a ser depositado.
     * @return A transação realizada.
     */
    @Operation(summary = "Realizar depósito",
            description = "Realiza um depósito em uma conta específica, aumentando o saldo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transacao.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou número da conta não informado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
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
    @Operation(summary = "Realizar saque", description = "Realiza um saque na conta com um valor específico, aplicando taxas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou valor inválido"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
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
    @Operation(summary = "Realizar transferência", description = "Realiza uma transferência entre duas contas, aplicando taxas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou valor inválido"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
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
    @Operation(
            summary = "Realizar transferências em lote",
            description = "Permite realizar múltiplas transferências simultaneamente. Retorna as transferências bem-sucedidas e as falhas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transferências processadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"sucesso\": [\n" +
                                    "    { \"contaOrigem\": \"12345\", \"contaDestino\": \"54321\", \"valor\": 100.0 },\n" +
                                    "    { \"contaOrigem\": \"67890\", \"contaDestino\": \"98765\", \"valor\": 50.0 }\n" +
                                    "  ],\n" +
                                    "  \"falhas\": [\n" +
                                    "    \"Erro na transferência de 12346 para 54322: Saldo insuficiente.\"\n" +
                                    "  ]\n" +
                                    "}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (dados inconsistentes ou incorretos)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro inesperado durante o processamento das transferências"
            )
    })
    @PostMapping("/lote")
    public ResponseEntity<Map<String, Object>> realizarTransferenciaEmLote(@RequestBody LoteTransferenciaDto loteTransferenciaDto) {
        List<TransferenciaDto> transferencias = loteTransferenciaDto.getTransferencias();

        List<TransferenciaDto> transferenciasBemSucedidas = new ArrayList<>();
        List<String> transferenciasFalhas = new ArrayList<>();

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

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return ResponseEntity.ok(
                Map.of(
                        "sucesso", transferenciasBemSucedidas,
                        "falhas", transferenciasFalhas
                )
        );
    }
}