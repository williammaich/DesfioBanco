package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.dto.ContaCorrenteDto;
import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/conta-corrente")
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
        }

    @PostMapping
    public ResponseEntity<ContaCorrente> criarConta(@Valid @RequestBody ContaCorrente contaCorrente) {
        ContaCorrente novaConta = contaCorrenteService.criarConta(contaCorrente);
        return ResponseEntity.ok(novaConta);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<ContaCorrente> buscarConta(@PathVariable("numero") String numero){
        return contaCorrenteService.buscarConta(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{numero}/atualizar-saldo")
    public ResponseEntity<ContaCorrenteDto> atualizarSaldo(@PathVariable("numero") String numero, @RequestBody ContaCorrente contaAtualizada) {
        ContaCorrente conta = contaCorrenteService.buscarConta(numero)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));

        conta.setSaldo(contaAtualizada.getSaldo());
        contaCorrenteService.atualizarSaldo(conta);

        // Converte para DTO
        ContaCorrenteDto dto = new ContaCorrenteDto(conta.getNumero(), conta.getSaldo());
        return ResponseEntity.ok(dto);
    }

}
