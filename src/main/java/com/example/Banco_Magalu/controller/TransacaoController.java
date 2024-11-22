package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.dto.TransferenciaDto;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.exception.ContaNaoEncontradaException;
import com.example.Banco_Magalu.exception.SaldoInsuficienteException;
import com.example.Banco_Magalu.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {


    private final TransacaoService transacaoService;

    // Construtor para injeção de dependência do TransacaoService
    public TransacaoController(TransacaoService transacaoService) {

        this.transacaoService = transacaoService;
    }

    /**
     * Endpoint para realizar uma transferência entre contas.
     *
     * @param transferenciaDto DTO com os dados da transferência.
     * @return A transação realizada.
     */
    @PostMapping("/transferencia")
    public ResponseEntity<?> realizarTransferencia(@Valid
                                                       @RequestBody TransferenciaDto
                                                               transferenciaDto,
                                                               BindingResult result) {
    try {
        //Realiza a transferência de valor entre as contas
        Transacao transacao = transacaoService.realizarTransferencia(
                transferenciaDto.getContaOrigem(),
                transferenciaDto.getContaDestino(),
                transferenciaDto.getValor());

        //Retorna a transação realizada
        return ResponseEntity.ok(transacao);
    }
    catch (ContaNaoEncontradaException e) {
        //Retorna uma resposta de erro 404 Not Found
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    catch (SaldoInsuficienteException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
    }
}
